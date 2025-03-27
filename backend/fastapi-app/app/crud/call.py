# app/crud/call.py (LangGraph 기반 재작성)
import asyncio
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

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
from app.exception.custom_exceptions import APIException
from app.exception.error_code import Error
from app.util.datetime_utils import now_kst
from app.util.message_utils import append_messages_to_call
from app.rag.store import store_call_history_embedding
from app.graph.call_graph import (
    build_create_call_graph,
    build_add_message_graph,
    build_end_call_graph,
)
from app.graph.nodes.memory import (
    convert_to_lc_message,
    safe_convert_message_to_dict,
)
import logging

logger = logging.getLogger(__name__)

create_call_graph = build_create_call_graph()
add_message_graph = build_add_message_graph()
end_call_graph = build_end_call_graph()


async def create_call(db: AsyncSession, request: StartCallRequest) -> StartCallResponse:
    state = {
        "call_id": -1,
        "member_id": request.memberId,
        "topic": request.topic,
        "messages": [],
    }

    try:
        result = await create_call_graph.ainvoke(state)
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

    db.add(new_call)
    await db.commit()
    await db.refresh(new_call)

    return StartCallResponse(
        callId=new_call.call_id,
        startTime=new_call.start_time,
        aiMessage=result.get("ai_response"),
        aiMessageKor=result.get("ai_response_kor"),
        aiAudioUrl=result.get("ai_audio_url"),
    )


async def add_message_to_call(
    db: AsyncSession, callId: int, request: UserMessageRequest
) -> AIMessageResponse:
    result = await db.execute(select(Call).where(Call.call_id == callId))
    call_record = result.scalars().first()

    if call_record.end_time is not None:
        raise APIException(400, Error.CALL_ALREADY_ENDED)

    if not call_record:
        raise APIException(404, Error.CALL_NOT_FOUND)

    state = {
        "call_id": call_record.call_id,
        "member_id": call_record.member_id,
        "input": request.userMessage,
        "messages": [convert_to_lc_message(Message(**m)) for m in call_record.messages],
    }

    try:
        result = await add_message_graph.ainvoke(state)
    except Exception:
        raise APIException(500, Error.CALL_INTERNAL_ERROR)

    append_messages_to_call(
        call_record, [safe_convert_message_to_dict(m) for m in result["messages"][-2:]]
    )

    db.add(call_record)
    await db.commit()
    await db.refresh(call_record)

    return AIMessageResponse(
        aiMessage=result.get("ai_response"),
        aiMessageKor=result.get("ai_response_kor"),
        aiAudioUrl=result.get("ai_audio_url"),
    )


async def end_call(
    db: AsyncSession, call_id: int, request: EndCallRequest
) -> EndCallResponse:
    result = await db.execute(select(Call).where(Call.call_id == call_id))
    call_record = result.scalars().first()

    if not call_record:
        raise APIException(404, Error.CALL_NOT_FOUND)

    if call_record.end_time is not None:
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

    db.add(call_record)
    await db.commit()
    await db.refresh(call_record)

    # 비동기 태스크로 따로 실행
    asyncio.create_task(
        store_call_history_embedding(
            call_id=call_record.call_id,
            member_id=call_record.member_id,
            messages=[Message(**m) for m in call_record.messages],
        )
    )

    return EndCallResponse(
        endTime=end_time,
        duration=duration,
        aiMessage=result.get("ai_response"),
        aiMessageKor=result.get("ai_response_kor"),
        aiAudioUrl=result.get("ai_audio_url"),
    )
