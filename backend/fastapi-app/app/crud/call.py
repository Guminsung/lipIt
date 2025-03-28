# app/crud/call.py (LangGraph 기반 재작성)
import asyncio
import json
from typing import Union
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.orm import sessionmaker

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
from app.crud import native_expression
from app.crud import report as report_crud
from app.graph.nodes.llm import llm
import logging
from langchain.schema import SystemMessage, HumanMessage

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
) -> Union[AIMessageResponse, EndCallResponse]:
    result = await db.execute(select(Call).where(Call.call_id == callId))
    call_record = result.scalars().first()

    if call_record.end_time is not None:
        raise APIException(400, Error.CALL_ALREADY_ENDED)

    if not call_record:
        raise APIException(404, Error.CALL_NOT_FOUND)

    # 시간 초과 여부 확인
    current_time = now_kst()
    call_duration = (current_time - call_record.start_time).total_seconds()
    is_timeout = call_duration >= 300  # 5분 = 300초

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

    should_end_call = result.get("should_end_call") is True or is_timeout

    # 통화 종료 조건이면 end_time 처리
    if should_end_call:
        end_time = now_kst()
        duration = int((end_time - call_record.start_time).total_seconds())

        call_record.end_time = end_time
        call_record.updated_at = end_time

        # RAG 저장 비동기로
        asyncio.create_task(
            store_call_history_embedding(
                call_id=call_record.call_id,
                member_id=call_record.member_id,
                messages=[Message(**m) for m in call_record.messages],
            )
        )

        db.add(call_record)
        await db.commit()
        await db.refresh(call_record)

        return EndCallResponse(
            endTime=end_time,
            duration=duration,
            aiMessage=result.get("ai_response"),
            aiMessageKor=result.get("ai_response_kor"),
            aiAudioUrl=result.get("ai_audio_url"),
        )

    # 일반 응답
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

    # 통화 종료 시 리포트 생성
    try:
        # 리포트 생성 요청 객체 생성
        from app.schema.report import CreateReportRequest
        
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
            max_length = 500  # 데이터베이스 필드 크기에 맞게 제한
            
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
        new_report = await report_crud.create_report(db=db, request=report_request)
        
        # 원어민 표현 생성 및 저장 (직접 호출로 변경)
        await generate_and_save_native_expressions(
            db=db, 
            call_id=call_record.call_id, 
            report_id=new_report.reportId
        )
    except Exception as e:
        logger.error(f"Failed to create report or native expressions: {str(e)}")
        # 리포트 생성 실패는 통화 종료에 영향을 주지 않도록 예외를 전파하지 않음

    return EndCallResponse(
        endTime=end_time,
        duration=duration,
        aiMessage=result.get("ai_response"),
        aiMessageKor=result.get("ai_response_kor"),
        aiAudioUrl=result.get("ai_audio_url"),
    )


async def generate_and_save_native_expressions(db: AsyncSession, call_id: int, report_id: int):
    """통화 내용을 분석하여 원어민 표현을 생성하고 저장합니다."""
    try:
        # 통화 메시지 가져오기
        result = await db.execute(select(Call).where(Call.call_id == call_id))
        call_record = result.scalars().first()
        
        if not call_record or not call_record.messages:
            return
        
        # 사용자 메시지만 필터링
        user_messages = [msg for msg in call_record.messages if msg.get("type") != "ai"]
        
        # 최대 5개까지 처리
        for idx, msg in enumerate(user_messages[:5]):
            user_sentence = msg.get("content", "")
            if not user_sentence.strip():
                continue
                
            # LLM을 사용하여 원어민 표현 생성
            system_prompt = """
            You are a helpful assistant that helps non-native English speakers improve their English expressions.
            
            Given a sentence from a user, suggest a more natural way a native speaker might express the same idea.
            
            Identify the SPECIFIC KEY PHRASE or expression in your suggested native expression that is most important or most commonly used by native speakers.
            
            For example, if the user says "I want to include AI features like hearing", you might suggest "I want to incorporate AI features such as auditory capabilities" and the key phrase would be "incorporate ... such as".
            
            Return your response in the following JSON format:
            {
              "native_expression": "The complete natural expression a native speaker would use",
              "keyword": "The specific key phrase or idiom that is important",
              "keyword_korean": "The Korean translation of just the key phrase"
            }
            
            Do not include any additional text outside the JSON.
            """
            
            user_prompt = f"Please improve this English expression: '{user_sentence}'"
            
            chat_prompt = [
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": user_prompt}
            ]
            
            try:
                # 각 반복에서 LLM 호출 및 데이터베이스 저장 작업을 완료
                response = await llm.ainvoke(chat_prompt)
                response_content = response.content.strip()
                
                # JSON 파싱
                response_json = json.loads(response_content)
                
                # 원어민 표현 저장 - 새로운 데이터베이스 세션을 사용하여 충돌 방지
                async with AsyncSession(db.bind, expire_on_commit=False) as new_session:
                    await native_expression.create_native_expression(
                        db=new_session,
                        report_id=report_id,
                        my_sentence=user_sentence,
                        ai_sentence=response_json.get("native_expression", ""),
                        keyword=response_json.get("keyword", ""),
                        keyword_korean=response_json.get("keyword_korean", "")
                    )
            except Exception as e:
                # JSON 파싱 오류 처리
                logger.error(f"Error parsing LLM response: {str(e)}")
                continue
                
    except Exception as e:
        # 오류 처리
        logger.error(f"Error generating native expressions: {str(e)}")
        return
