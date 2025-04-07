# app/service/call.py
import asyncio
import logging

from app.service.voice import get_voice_by_call_id, get_voice_by_member_id
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import text
from app.graph.call_graph import (
    build_add_message_graph,
    build_start_call_graph,
    build_end_call_graph,
)
from app.graph.node.memory import convert_to_lc_message, safe_convert_message_to_dict
from app.service.report import generate_report
from app.util.datetime_utils import now_kst, to_kst, to_kst_isoformat
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
    EndCallResponse,
)

logger = logging.getLogger(__name__)

start_call_graph = build_start_call_graph()
add_message_graph = build_add_message_graph()
end_call_graph = build_end_call_graph()


async def start_call(
    db: AsyncSession, request: StartCallRequest, member_id: int=1
) -> StartCallResponse:
    voice = await get_voice_by_member_id(db, member_id)
    
    # 자유 주제(topic = None)인 경우 뉴스/날씨 데이터로 topic 추출
    topic = request.topic
    if not topic:
        news = await get_random_news(db, category="경제")  # 사회, 생활, 날씨, 경제, IT
        topic = news.title if news else ""

    state = {
        "call_id": -1,
        "member_id": member_id,
        "topic": topic,
        "messages": [],
        "voice_name": voice.voice_name,
        "voice_type": voice.type,
    }

    try:
        result = await start_call_graph.ainvoke(state)
    except Exception:
        raise APIException(500, Error.CALL_AI_FAILED)

    ai_message = result["messages"][-1]
    new_call = Call(
        member_id=request.memberId,
        messages=[safe_convert_message_to_dict(ai_message)],
        start_time=now_kst(),
        end_time=None,
    )

    await save_call(db, new_call)

    return StartCallResponse(
        calld=new_call.call_id,
        startTime=to_kst_isoformat(new_call.start_time),
        aiMessage=result.get("ai_response"),
        aiMessageKor=result.get("ai_response_kor"),
        aiAudioUrl=result.get("ai_audio_url"),
    )


async def add_message_to_call(
    db: AsyncSession,
    call_id: int,
    request: UserMessageRequest,
    member_id: int=1,
) -> AIMessageResponse:
    call_record = await get_call_by_id(db, call_id)
    if not call_record:
        raise APIException(404, Error.CALL_NOT_FOUND)
    if call_record.end_time:
        raise APIException(400, Error.CALL_ALREADY_ENDED)
    voice = await get_voice_by_call_id(db, call_id)

    # 시간 초과 여부 확인
    duration = int((now_kst() - to_kst(call_record.start_time)).total_seconds())
    is_timeout = duration >= 300  # 5분

    state = {
        "call_id": call_record.call_id,
        "member_id": member_id,
        "input": request.userMessage,
        "messages": [convert_to_lc_message(Message(**m)) for m in call_record.messages],
        "is_timeout": is_timeout,
        "voice_name": voice.voice_name,
        "voice_type": voice.type,
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

    print(f"📌 is_timeout = {is_timeout}")
    print(f"📌 should_end_call = {should_end}")

    if should_end:
        end_time = now_kst()
        call_record.duration = duration
        call_record.end_time = end_time
        call_record.updated_at = end_time

        await save_call(db, call_record)

        # member의 total_report_count 증가 - raw SQL 사용
        try:
            await db.execute(
                text(
                    """
                    UPDATE member 
                    SET total_report_count = COALESCE(total_report_count, 0) + 1,
                        total_call_duration = COALESCE(total_call_duration, 0) + :duration
                    WHERE member_id = :member_id
                """
                ),
                {"member_id": call_record.member_id, "duration": duration},
            )
            await db.commit()
            logger.info(
                f"Member {call_record.member_id}의 total_report_count를 증가시키고 total_call_duration에 {duration}을 추가했습니다."
            )
        except Exception as e:
            logger.error(f"total_report_count/total_call_duration 업데이트 실패: {str(e)}")
            # 이 오류로 인해 전체 흐름이 중단되지 않도록 pass
            pass

        # "human" 메시지의 content 길이 합산
        total_human_content_length = sum(
            len(m.get("content", ""))
            for m in call_record.messages
            if m.get("type") == "human"
        )

        # 조건에 따라 reportCreated 설정
        reportCreated = total_human_content_length > 100

        if reportCreated:
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

        return AIMessageResponse(
            aiMessage=result["ai_response"],
            aiMessageKor=result["ai_response_kor"],
            endTime=to_kst_isoformat(end_time),
            duration=duration,
            reportCreated=reportCreated,
        )

    # 일반 응답
    await save_call(db, call_record)
    return AIMessageResponse(
        aiMessage=result["ai_response"], aiMessageKor=result["ai_response_kor"]
    )


async def end_call(db: AsyncSession, call_id: int) -> EndCallResponse:
    call_record = await get_call_by_id(db, call_id)

    if not call_record:
        raise APIException(404, Error.CALL_NOT_FOUND)

    if call_record.end_time:
        raise APIException(400, Error.CALL_ALREADY_ENDED)

    end_time = now_kst()
    duration = int((end_time - to_kst(call_record.start_time)).total_seconds())

    call_record.duration = duration
    call_record.end_time = end_time
    call_record.updated_at = end_time

    await save_call(db, call_record)

    # member의 total_report_count 증가 - raw SQL 사용
    try:
        await db.execute(
            text(
                """
                UPDATE member 
                SET total_report_count = COALESCE(total_report_count, 0) + 1,
                    total_call_duration = COALESCE(total_call_duration, 0) + :duration
                WHERE member_id = :member_id
            """
            ),
            {"member_id": call_record.member_id, "duration": duration},
        )
        await db.commit()
        logger.info(
            f"Member {call_record.member_id}의 total_report_count를 증가시키고 total_call_duration에 {duration}을 추가했습니다."
        )
    except Exception as e:
        logger.error(f"total_report_count/total_call_duration 업데이트 실패: {str(e)}")
        # 이 오류로 인해 전체 흐름이 중단되지 않도록 pass
        pass

    # "human" 메시지의 content 길이 합산
    total_human_content_length = sum(
        len(m.get("content", ""))
        for m in call_record.messages
        if m.get("type") == "human"
    )

    # 조건에 따라 reportCreated 설정
    reportCreated = total_human_content_length > 100

    if reportCreated:
        # 리포트 생성 (비동기 태스크)
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
        reportCreated=reportCreated,
    )
