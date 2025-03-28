# app/service/call.py
import asyncio
import logging
from typing import Union

from sqlalchemy.ext.asyncio import AsyncSession
from app.graph.call_graph import (
    build_add_message_graph,
    build_start_call_graph,
    build_end_call_graph,
)
from app.graph.node.memory import convert_to_lc_message, safe_convert_message_to_dict
from app.rag.store import store_call_history_embedding
from app.service.report import generate_report
from app.util.datetime_utils import now_kst, to_kst_isoformat
from app.util.message_utils import append_messages_to_call
from app.exception.custom_exceptions import APIException
from app.exception.error_code import Error
from app.crud.call import get_call_by_id, save_call
from app.crud.news import get_random_news
from app.model.call import Call
from app.schema.call import (
    AIMessageResponse,
    StartCallRequest,
    StartCallResponse,
    Message,
    UserMessageRequest,
    EndCallRequest,
    EndCallResponse,
)

logger = logging.getLogger(__name__)

start_call_graph = build_start_call_graph()
add_message_graph = build_add_message_graph()
end_call_graph = build_end_call_graph()


async def start_call(db: AsyncSession, request: StartCallRequest) -> StartCallResponse:
    # 자유 주제(topic = None)인 경우 뉴스/날씨 데이터로 topic 추출
    topic = request.topic
    if not topic:
        news = await get_random_news(db, category="경제")  # 사회, 생활, 날씨, 경제, IT
        topic = news.title if news else ""

    state = {
        "call_id": -1,
        "member_id": request.memberId,
        "topic": topic,
        "messages": [],
    }

    try:
        result = await start_call_graph.ainvoke(state)
    except Exception:
        raise APIException(500, Error.CALL_AI_FAILED)

    ai_message = result["messages"][-1]
    new_call = Call(
        call_request_id=request.callRequestId,
        member_id=request.memberId,
        messages=[safe_convert_message_to_dict(ai_message)],
        start_time=now_kst(),
        end_time=None,
    )

    await save_call(db, new_call)

    return StartCallResponse(
        callId=new_call.call_id,
        startTime=to_kst_isoformat(new_call.start_time),
        aiMessage=result.get("ai_response"),
        aiMessageKor=result.get("ai_response_kor"),
        aiAudioUrl=result.get("ai_audio_url"),
    )


async def add_message_to_call(
    db: AsyncSession, call_id: int, request: UserMessageRequest
) -> Union[AIMessageResponse, EndCallResponse]:
    call_record = await get_call_by_id(db, call_id)
    if not call_record:
        raise APIException(404, Error.CALL_NOT_FOUND)
    if call_record.end_time:
        raise APIException(400, Error.CALL_ALREADY_ENDED)

    # 시간 초과 여부 확인
    is_timeout = (now_kst() - call_record.start_time).total_seconds() >= 300  # 5분

    state = {
        "call_id": call_record.call_id,
        "member_id": call_record.member_id,
        "input": request.userMessage,
        "messages": [convert_to_lc_message(Message(**m)) for m in call_record.messages],
        "is_timeout": is_timeout,
    }

    try:
        result = await add_message_graph.ainvoke(state)
    except Exception:
        raise APIException(500, Error.CALL_INTERNAL_ERROR)

    append_messages_to_call(
        call_record, [safe_convert_message_to_dict(m) for m in result["messages"][-2:]]
    )

    # 통화 종료 조건이면 end_time 처리
    should_end = result.get("should_end_call") or is_timeout
    if should_end:
        end_time = now_kst()
        call_record.end_time = end_time
        call_record.updated_at = end_time

        await save_call(db, call_record)

        # RAG Embedding 저장
        asyncio.create_task(
            store_call_history_embedding(
                call_id=call_record.call_id,
                member_id=call_record.member_id,
                messages=[Message(**m) for m in call_record.messages],
            )
        )

        duration = int((end_time - call_record.start_time).total_seconds())

        # 리포트 생성
        asyncio.create_task(
            generate_report(
                db=db,
                member_id=call_record.member_id,
                call_id=call_record.call_id,
                duration=duration,
                messages=[Message(**m) for m in call_record.messages],
            )
        )

        return EndCallResponse(
            endTime=to_kst_isoformat(end_time),
            duration=int((end_time - call_record.start_time).total_seconds()),
            aiMessage=result["ai_response"],
            aiMessageKor=result["ai_response_kor"],
            aiAudioUrl=result["ai_audio_url"],
        )

    # 일반 응답
    await save_call(db, call_record)
    return AIMessageResponse(
        aiMessage=result["ai_response"],
        aiMessageKor=result["ai_response_kor"],
        aiAudioUrl=result["ai_audio_url"],
    )


async def end_call(
    db: AsyncSession, call_id: int, request: EndCallRequest
) -> EndCallResponse:
    call_record = await get_call_by_id(db, call_id)
    if not call_record:
        raise APIException(404, Error.CALL_NOT_FOUND)
    if call_record.end_time:
        raise APIException(400, Error.CALL_ALREADY_ENDED)

    state = {
        "call_id": call_record.call_id,
        "member_id": call_record.member_id,
        "input": request.userMessage,
        "messages": [convert_to_lc_message(Message(**m)) for m in call_record.messages],
    }

    try:
        result = await end_call_graph.ainvoke(state)
    except Exception:
        raise APIException(500, Error.CALL_INTERNAL_ERROR)

    end_time = now_kst()
    duration = int((end_time - call_record.start_time).total_seconds())

    call_record.end_time = end_time
    call_record.updated_at = end_time

    append_messages_to_call(
        call_record, [safe_convert_message_to_dict(m) for m in result["messages"][-2:]]
    )
    await save_call(db, call_record)

    asyncio.create_task(
        store_call_history_embedding(
            call_id=call_record.call_id,
            member_id=call_record.member_id,
            messages=[Message(**m) for m in call_record.messages],
        )
    )

    # 리포트 생성
    asyncio.create_task(
        generate_report(
            db=db,
            member_id=call_record.member_id,
            call_id=call_record.call_id,
            duration=duration,
            messages=[Message(**m) for m in call_record.messages],
        )
    )

    return EndCallResponse(
        endTime=to_kst_isoformat(end_time),
        duration=duration,
        aiMessage=result["ai_response"],
        aiMessageKor=result["ai_response_kor"],
        aiAudioUrl=result["ai_audio_url"],
    )
