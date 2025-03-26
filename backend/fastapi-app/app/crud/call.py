# app/crud/call.py (LangGraph 기반 재작성)
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
from app.exception.error_code import ErrorCode
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
        raise APIException(
            500, "AI 응답 생성에 실패했습니다.", ErrorCode.CALL_AI_FAILED
        )

    ai_text = result.get("ai_response")
    ai_audio_url = result.get("ai_audio_url")
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
        aiFirstMessage=ai_text,
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

    state = {
        "call_id": call_record.call_id,
        "member_id": call_record.member_id,
        "input": request.userMessage,
        "messages": [convert_to_lc_message(Message(**m)) for m in call_record.messages],
    }

    try:
        result = await add_message_graph.ainvoke(state)
    except Exception:
        raise APIException(
            500,
            "메시지 처리 중 서버 오류가 발생했습니다.",
            ErrorCode.CALL_INTERNAL_ERROR,
        )

    append_messages_to_call(
        call_record, [safe_convert_message_to_dict(m) for m in result["messages"][-2:]]
    )

    db.add(call_record)
    await db.commit()
    await db.refresh(call_record)

    return AIMessageResponse(
        aiMessage=result.get("ai_response"),
        aiAudioUrl=result.get("ai_audio_url"),
    )


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

    state = {
        "call_id": call_record.call_id,
        "member_id": call_record.member_id,
        "input": request.userMessage,
        "messages": [convert_to_lc_message(Message(**m)) for m in call_record.messages],
    }

    try:
        result = await end_call_graph.ainvoke(state)
    except Exception:
        raise APIException(
            500, "통화 종료 중 서버 오류가 발생했습니다.", ErrorCode.CALL_INTERNAL_ERROR
        )

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

    await store_call_history_embedding(
        call_id=call_record.call_id,
        member_id=call_record.member_id,
        messages=[Message(**m) for m in call_record.messages],
    )

    return EndCallResponse(
        callId=call_record.call_id,
        endTime=end_time,
        duration=duration,
        aiEndMessage=result.get("ai_response"),
    )
