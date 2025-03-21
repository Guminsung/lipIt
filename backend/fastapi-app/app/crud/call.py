import os
import json, time
import httpx
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from app.models.call import Call
from app.schemas.call import (
    AIMessageResponse,
    CallRequest,
    CallResponse,
    Message,
    UserMessageRequest,
)
from datetime import datetime


async def create_call(db: AsyncSession, request: CallRequest) -> CallResponse:
    """새로운 통화 레코드 생성 및 AI 첫 메시지 저장"""
    ai_message = Message(
        role="ai",
        text="Test Message",  # 실제로는 OpenAI API 응답
        text_kor=None,
        audio_url=None,
        timestamp=int(time.time()),
    )

    new_call = Call(
        call_history_id=request.userId,
        messages=json.dumps([ai_message.dict()]),  # JSON 직렬화
        start_time=datetime.now(),
        end_time=None,
    )

    db.add(new_call)
    await db.commit()
    await db.refresh(new_call)

    return CallResponse(
        callId=new_call.call_id,
        startTime=new_call.start_time,
        aiFirstMessage=ai_message.text,
    )


OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
S3_BASE_URL = "https://s3.amazonaws.com/ai_audio/"  # AI 음성 파일 저장 경로


async def generate_ai_response(user_message: str) -> str:
    """사용자 메시지를 기반으로 AI 응답 생성"""
    async with httpx.AsyncClient() as client:
        # response = await client.post(
        #     "https://api.openai.com/v1/chat/completions",
        #     headers={
        #         "Authorization": f"Bearer {OPENAI_API_KEY}",
        #         "Content-Type": "application/json",
        #     },
        #     json={
        #         "model": "gpt-4",
        #         "messages": [{"role": "user", "content": user_message}],
        #     },
        # )
        # if response.status_code == 200:
        #     return response.json()["choices"][0]["message"]["content"]
        # else:
        #     raise Exception("OpenAI API 호출 실패")
        return "AI Response"


async def create_ai_audio(ai_message: str) -> str:
    """AI 응답 메시지를 기반으로 음성 파일 생성 (S3 저장 경로 반환)"""
    audio_file_id = f"response_{int(datetime.utcnow().timestamp())}.mp3"
    return f"{S3_BASE_URL}{audio_file_id}"


async def add_message_to_call(
    db: AsyncSession, callId: int, request: UserMessageRequest
) -> AIMessageResponse:
    """사용자 메시지를 저장하고, AI 응답을 생성하여 통화 메시지 리스트에 추가"""
    result = await db.execute(select(Call).where(Call.call_id == callId))
    call_record = result.scalars().first()

    if not call_record:
        raise Exception("해당 통화 기록을 찾을 수 없습니다.")

    # 사용자 메시지 생성
    user_message_data = {
        "role": "user",
        "text": request.userMessage,
        "text_kor": request.userMessageKor,
        "audio_url": str(request.userAudioUrl) if request.userAudioUrl else None,
        "timestamp": int(time.time()),
    }

    # AI 응답 생성
    ai_message_text = await generate_ai_response(request.userMessage)
    ai_audio_url = await create_ai_audio(ai_message_text)

    ai_message_data = {
        "role": "ai",
        "text": ai_message_text,
        "text_kor": None,  # AI가 한국어 번역을 지원하면 추가 가능
        "audio_url": str(ai_audio_url),
        "timestamp": int(time.time()),
    }

    # 기존 messages 데이터 로드 (str이면 JSON으로 변환)
    if call_record.messages:
        if isinstance(call_record.messages, str):
            call_record.messages = json.loads(call_record.messages)
    else:
        call_record.messages = []

    # 리스트에 새로운 메시지 추가
    new_messages = call_record.messages + [
        user_message_data,
        ai_message_data,
    ]  # 새로운 리스트 생성

    # SQLAlchemy에 필드 업데이트를 명확하게 인식시키기 위해 새로운 객체로 설정
    call_record.messages = new_messages
    call_record.updated_at = datetime.utcnow()

    # 데이터베이스에 변경사항 저장
    db.add(call_record)  # 🔥 변경된 객체를 명확하게 SQLAlchemy에 추가
    await db.commit()
    await db.refresh(call_record)

    return AIMessageResponse(aiMessage=ai_message_text, aiAudioUrl=ai_audio_url)
