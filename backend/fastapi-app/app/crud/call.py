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
        raise APIException(400, "이미 종료된 통화입니다.", ErrorCode.CALL_ALREADY_ENDED)

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
        aiMessageKor=result.get("ai_response_kor"),
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

    # 통화 종료 시 리포트 생성
    try:
        from app.crud.report import create_report
        from app.schema.report import CreateReportRequest
        from app.graph.nodes.llm import llm
        from langchain.schema import SystemMessage, HumanMessage
        import json
        
        # 간단한 대화 분석
        messages = [Message(**m) for m in call_record.messages]
        word_count = sum(len(m.content.split()) for m in messages if hasattr(m, 'content') and m.content)
        sentence_count = sum(len(m.content.split('.')) for m in messages if hasattr(m, 'content') and m.content)
        
        # 대화 내용을 문자열로 변환
        conversation_text = "\n".join([
            f"{'AI' if m.type == 'ai' else '사용자'}: {m.content}" 
            for m in messages if hasattr(m, 'content') and m.content
        ])
        
        # 기본 요약 및 피드백
        communication_summary = "통화 내용 요약"
        feedback_summary = "피드백 요약"
        
        # 기존 llm을 사용하여 요약 및 피드백 생성
        try:
            # 요약 생성 프롬프트
            summary_prompt = [
                SystemMessage(content="""대화 내용을 한국어로 간결하게 요약해주세요. 
                중요한 주제와 결론을 포함해야 합니다. 
                100단어 이내로 짧게 작성해주세요."""),
                HumanMessage(content=conversation_text)
            ]
            
            # 피드백 생성 프롬프트
            feedback_prompt = [
                SystemMessage(content="""대화에서 사용자의 의사소통 패턴과 개선할 점에 대한 피드백을 한국어로 제공해주세요.
                구체적인 예시와 조언을 포함해야 합니다.
                100단어 이내로 짧게 작성해주세요."""),
                HumanMessage(content=conversation_text)
            ]
            
            # 요약 생성
            summary_response = await llm.ainvoke(summary_prompt)
            communication_summary = summary_response.content.strip()
            
            # 피드백 생성
            feedback_response = await llm.ainvoke(feedback_prompt)
            feedback_summary = feedback_response.content.strip()
            
            # 텍스트 길이 제한 및 특수 문자 처리
            max_length = 500  # 데이터베이스 필드 크기에 맞게 더 제한
            
            # 텍스트 클리닝 함수
            def clean_text(text):
                # 줄바꿈, 따옴표, 기타 문제가 될 수 있는 문자 제거
                text = text.replace('\n', ' ').replace('\r', ' ')
                text = text.replace('"', "'")  # 큰따옴표를 작은따옴표로 변경
                text = text.replace('\\', '')  # 백슬래시 제거
                return text[:max_length]  # 길이 제한
                
            communication_summary = clean_text(communication_summary)
            feedback_summary = clean_text(feedback_summary)
        except Exception as e:
            logger.error(f"AI 요약/피드백 생성 실패: {str(e)}")
            # 생성 실패 시 기본 메시지 사용
        
        # 리포트 생성 요청
        report_request = CreateReportRequest(
            memberId=call_record.member_id,
            callId=call_record.call_id,
            callDuration=duration,
            celebVideoUrl=None,
            wordCount=word_count,
            sentenceCount=sentence_count,
            communicationSummary=communication_summary,
            feedbackSummary=feedback_summary
        )
        
        # 리포트 생성
        await create_report(db, report_request)
    except Exception as e:
        # 리포트 생성에 실패해도 통화 종료는 계속 진행
        logger.error(f"리포트 생성 실패: {str(e)}")

    return EndCallResponse(
        endTime=end_time,
        duration=duration,
        aiMessage=result.get("ai_response"),
        aiMessageKor=result.get("ai_response_kor"),
        aiAudioUrl=result.get("ai_audio_url"),
    )
