from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from typing import List, Optional
import json

from app.model.report import Report
from app.model.call import Call
from app.schema.report import ReportResponse, CreateReportRequest, ReportSummaryResponse, ReportScriptResponse, ScriptItem, ReportExpressionsResponse, NativeExpressionItem
from app.exception.custom_exceptions import APIException
from app.exception.error_code import Error
from datetime import datetime
from app.graph.nodes.llm import llm


# 회원 ID로 리포트 목록 조회
async def get_reports_by_member_id(db: AsyncSession, member_id: int) -> List[ReportResponse]:
    query = select(Report).where(Report.member_id == member_id).order_by(Report.created_at.desc())
    result = await db.execute(query)
    reports = result.scalars().all()
    
    # 리포트 응답 객체 리스트로 변환
    report_responses = []
    for report in reports:
        report_responses.append(ReportResponse(
            reportId=report.report_id,
            callDuration=report.call_duration,
            celebVideoUrl=report.celeb_video_url,
            wordCount=report.word_count,
            sentenceCount=report.sentence_count,
            communicationSummary=report.communication_summary,
            feedbackSummary=report.feedback_summary,
            createdAt=report.created_at.strftime("%Y-%m-%d")
        ))
    
    return report_responses


# 새 리포트 생성
async def create_report(db: AsyncSession, request: CreateReportRequest) -> ReportResponse:
    report = Report(
        member_id=request.memberId,
        call_id=request.callId,
        call_duration=request.callDuration,
        celeb_video_url=request.celebVideoUrl,
        word_count=request.wordCount,
        sentence_count=request.sentenceCount,
        communication_summary=request.communicationSummary,
        feedback_summary=request.feedbackSummary
    )
    
    db.add(report)
    await db.commit()
    await db.refresh(report)
    
    return ReportResponse(
        reportId=report.report_id,
        callDuration=report.call_duration,
        celebVideoUrl=report.celeb_video_url,
        wordCount=report.word_count,
        sentenceCount=report.sentence_count,
        communicationSummary=report.communication_summary,
        feedbackSummary=report.feedback_summary,
        createdAt=report.created_at.strftime("%Y-%m-%d")
    )


# 리포트 요약 조회
async def get_report_summary(db: AsyncSession, report_id: int) -> ReportSummaryResponse:
    report = await db.get(Report, report_id)
    
    if not report:
        raise APIException(404, Error.REPORT_NOT_FOUND)
    
    return ReportSummaryResponse(
        callDuration=report.call_duration,
        wordCount=report.word_count,
        sentenceCount=report.sentence_count,
        communicationSummary=report.communication_summary,
        feedbackSummary=report.feedback_summary,
        createdAt=report.created_at.strftime("%Y-%m-%d")
    )


# 리포트 스크립트 조회
async def get_report_script(db: AsyncSession, report_id: int) -> ReportScriptResponse:
    # 리포트 조회
    report = await db.get(Report, report_id)
    
    if not report:
        raise APIException(404, Error.REPORT_NOT_FOUND)
    
    # 해당 리포트와 연결된 통화 조회
    call_id = report.call_id
    result = await db.execute(select(Call).where(Call.call_id == call_id))
    call = result.scalars().first()
    
    if not call or not call.messages:
        return ReportScriptResponse(script=[])
    
    # 통화 메시지를 스크립트 아이템으로 변환
    script_items = []
    for message in call.messages:
        is_ai = message.get("type") == "ai"
        content_kor = message.get("content_kor")
        if content_kor is None:
            content_kor = ""
            
        script_items.append(
            ScriptItem(
                isAI=is_ai,
                content=message.get("content", ""),
                contentKor=content_kor,
                timestamp=message.get("timestamp", "")
            )
        )
    
    return ReportScriptResponse(script=script_items)


# 리포트 원어민 표현 생성 및 조회
async def get_report_expressions(db: AsyncSession, report_id: int) -> ReportExpressionsResponse:
    # 리포트 조회
    report = await db.get(Report, report_id)
    
    if not report:
        raise APIException(404, Error.REPORT_NOT_FOUND)
    
    # 해당 리포트와 연결된 통화 조회
    call_id = report.call_id
    result = await db.execute(select(Call).where(Call.call_id == call_id))
    call = result.scalars().first()
    
    if not call or not call.messages:
        return ReportExpressionsResponse(nativeExpressions=[])
    
    # 사용자 메시지만 추출 (human 타입)
    user_messages = [msg for msg in call.messages if msg.get("type") != "ai"]
    
    # 원어민 표현 생성
    expressions = []
    expression_id = 1
    
    # 최대 5개의 표현만 생성
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
            response = await llm.ainvoke(chat_prompt)
            response_content = response.content.strip()
            
            # JSON 파싱
            response_json = json.loads(response_content)
            
            expressions.append(
                NativeExpressionItem(
                    nativeExpressionId=expression_id,
                    mySentence=user_sentence,
                    AISentence=response_json.get("native_expression", ""),
                    keyword=response_json.get("keyword", ""),
                    keywordKorean=response_json.get("keyword_korean", "")
                )
            )
            expression_id += 1
        except Exception as e:
            # 개별 표현 생성 실패 시 건너뛰기
            continue
    
    return ReportExpressionsResponse(nativeExpressions=expressions) 