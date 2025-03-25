# app/service/call_service.py

import os
import time
from datetime import datetime, timezone, timedelta
import httpx

from dotenv import load_dotenv
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.model.call import Call
from app.rag.search import search_relevant_call_memory
from app.rag.store import store_call_history_embedding
from app.schema.call import (
    AIMessageResponse,
    StartCallRequest,
    StartCallResponse,
    Message,
    UserMessageRequest,
    EndCallRequest,
    EndCallResponse,
)
from app.exception.custom_exceptions import APIException
from app.exception.error_code import ErrorCode
from app.util.message_utils import append_messages_to_call

load_dotenv()

OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
S3_BASE_URL = "https://s3.amazonaws.com/ai_audio/"
KST = timezone(timedelta(hours=9))


async def create_call(db: AsyncSession, request: StartCallRequest) -> StartCallResponse:
    try:
        ai_text = await generate_ai_response(
            f"Start a phone conversation about this topic: {request.topic}",
            is_topic_prompt=True,
        )
    except Exception:
        raise APIException(
            500, "AI 응답 생성에 실패했습니다.", ErrorCode.CALL_AI_FAILED
        )

    try:
        ai_audio_url = await create_ai_audio(ai_text)
    except Exception:
        raise APIException(
            500, "AI 음성 생성에 실패했습니다.", ErrorCode.CALL_TTS_FAILED
        )

    ai_message = Message(
        role="ai",
        text=ai_text,
        text_kor=None,
        audio_url=ai_audio_url,
        timestamp=datetime.now(KST).isoformat(),
    )

    new_call = Call(
        call_request_id=request.callRequestId,
        member_id=request.memberId,
        messages=[ai_message.model_dump()],
        start_time=datetime.now(KST),
        end_time=None,
    )

    db.add(new_call)
    await db.commit()
    await db.refresh(new_call)

    return StartCallResponse(
        callId=new_call.call_id,
        startTime=new_call.start_time,
        aiFirstMessage=ai_message.text,
    )


async def add_message_to_call(
    db: AsyncSession, callId: int, request: UserMessageRequest
) -> AIMessageResponse:
    result = await db.execute(select(Call).where(Call.call_id == callId))
    call_record = result.scalars().first()

    if not call_record:
        raise APIException(
            404, "해당 통화 기록을 찾을 수 없습니다.", ErrorCode.CALL_NOT_FOUND
        )

    user_message = Message(
        role="user",
        text=request.userMessage,
        text_kor=request.userMessageKor,
        audio_url=str(request.userAudioUrl) if request.userAudioUrl else None,
        timestamp=datetime.now(KST).isoformat(),
    )

    ai_text = await generate_ai_response_from_history(
        member_id=call_record.member_id,
        messages=[Message(**m) for m in call_record.messages],
        user_input=request.userMessage,
    )
    ai_audio_url = await create_ai_audio(ai_text)

    ai_message = Message(
        role="ai",
        text=ai_text,
        text_kor=None,
        audio_url=ai_audio_url,
        timestamp=datetime.now(KST).isoformat(),
    )

    append_messages_to_call(call_record, [user_message, ai_message])
    db.add(call_record)
    await db.commit()
    await db.refresh(call_record)

    return AIMessageResponse(aiMessage=ai_text, aiAudioUrl=ai_audio_url)


async def end_call(
    db: AsyncSession, call_id: int, request: EndCallRequest
) -> EndCallResponse:
    result = await db.execute(select(Call).where(Call.call_id == call_id))
    call_record = result.scalars().first()

    if not call_record:
        raise APIException(
            404, "해당 통화 기록을 찾을 수 없습니다.", ErrorCode.CALL_NOT_FOUND
        )

    if call_record.end_time is not None:
        raise APIException(400, "이미 종료된 통화입니다.", ErrorCode.CALL_ALREADY_ENDED)

    end_time = datetime.now(KST)
    duration = int((end_time - call_record.start_time).total_seconds())

    user_message = Message(
        role="user",
        text=request.userResponse,
        audio_url=None,
        timestamp=datetime.now(KST).isoformat(),
    )

    ai_text = await generate_ai_response(
        "End the phone conversation in a friendly and natural way."
    )
    ai_audio_url = await create_ai_audio(ai_text)

    ai_message = Message(
        role="ai",
        text=ai_text,
        text_kor=None,
        audio_url=ai_audio_url,
        timestamp=datetime.now(KST).isoformat(),
    )

    append_messages_to_call(call_record, [user_message, ai_message])

    call_record.end_time = end_time
    call_record.updated_at = end_time

    db.add(call_record)
    await db.commit()
    await db.refresh(call_record)

    await store_call_history_embedding(
        call_id=call_record.call_id,
        member_id=call_record.member_id,
        messages=[Message(**m) for m in call_record.messages],
    )

    return EndCallResponse(
        callId=call_record.call_id,
        endTime=end_time,
        duration=duration,
        aiEndMessage=ai_text,
    )


async def generate_ai_response(user_input: str, is_topic_prompt: bool = False) -> str:
    system_prompt = (
        "You are currently on a phone call with the user. Speak naturally and casually in English."
        if not is_topic_prompt
        else "You are starting a phone call with the user. Greet them casually based on the topic."
    )

    try:
        async with httpx.AsyncClient() as client:
            response = await client.post(
                "https://api.openai.com/v1/chat/completions",
                headers={
                    "Authorization": f"Bearer {OPENAI_API_KEY}",
                    "Content-Type": "application/json",
                },
                json={
                    "model": "gpt-3.5-turbo",
                    "messages": [
                        {"role": "system", "content": system_prompt},
                        {"role": "user", "content": user_input},
                    ],
                    "temperature": 0.8,
                    "max_tokens": 150,
                },
            )
        if response.status_code == 200:
            return response.json()["choices"][0]["message"]["content"]
        raise APIException(500, "OpenAI API 호출 실패", ErrorCode.CALL_AI_FAILED)
    except Exception:
        raise APIException(500, "OpenAI 호출 중 예외 발생", ErrorCode.CALL_AI_FAILED)


async def generate_ai_response_from_history(
    member_id: int,
    messages: list[Message],
    user_input: str,
    similarity_threshold: float = 0.5,
    max_contexts: int = 3,
) -> str:
    # RAG 검색
    memory_matches = await search_relevant_call_memory(
        member_id=member_id, query=user_input, top_k=10
    )
    filtered_contexts = [
        match["content"]
        for match in memory_matches
        if match["score"] >= similarity_threshold
    ][:max_contexts]

    # 현재 메시지 히스토리 텍스트
    history_text = "\n".join(
        [f"{m.role.capitalize()}: {m.text}" for m in messages[-10:]]
    )

    # 전체 프롬프트 구성
    prompt = f"""
You are on a phone call with a user.

Here is the recent conversation:
{history_text}

Below are relevant things they mentioned in previous conversations:
{chr(10).join(filtered_contexts)}

Continue the conversation naturally based on the user's latest message:
User: {user_input}
AI:"""

    return await generate_ai_response(prompt, is_topic_prompt=False)


async def create_ai_audio(ai_message: str) -> str:
    audio_file_id = f"response_{int(datetime.now().timestamp())}.mp3"
    return f"{S3_BASE_URL}{audio_file_id}"
