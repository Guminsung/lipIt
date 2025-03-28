# app/service/report.py
# import logging
# from langchain.schema import SystemMessage, HumanMessage
# from app.crud import report
# from app.schema.report import CreateReportRequest
# from app.crud.report import create_report
# from app.service.native_expression import generate_and_save_native_expressions

# logger = logging.getLogger(__name__)


# async def generate_and_save_report(
#     db, member_id: int, call_id: int, duration: int, messages
# ):
#     """
#     통화 종료 시 리포트 생성
#     """
#     try:
#         # 간단한 대화 분석
#         word_count = sum(
#             len(m.content.split())
#             for m in messages
#             if hasattr(m, "content") and m.content
#         )
#         sentence_count = sum(
#             len(m.content.split("."))
#             for m in messages
#             if hasattr(m, "content") and m.content
#         )

#         # 대화 내용을 문자열로 변환
#         conversation_text = "\n".join(
#             [
#                 f"{'AI' if m.type == 'ai' else '사용자'}: {m.content}"
#                 for m in messages
#                 if hasattr(m, "content") and m.content
#             ]
#         )

#         # 기본 요약 및 피드백
#         communication_summary = "통화 내용 요약"
#         feedback_summary = "피드백 요약"

#         # 기존 llm을 사용하여 요약 및 피드백 생성
#         try:
#             # 요약 생성 프롬프트
#             summary_prompt = [
#                 SystemMessage(
#                     content="대화 내용을 한국어로 간결하게 요약해주세요. 중요한 주제와 결론 포함. 100단어 이내."
#                 ),
#                 HumanMessage(content=conversation_text),
#             ]

#             # 피드백 생성 프롬프트
#             feedback_prompt = [
#                 SystemMessage(
#                     content="대화에서 사용자의 의사소통 패턴과 개선할 점을 한국어로 100단어 이내로 요약해주세요. 구체적인 예시와 조언을 포함."
#                 ),
#                 HumanMessage(content=conversation_text),
#             ]

#             # 요약 생성
#             summary_response = await llm.ainvoke(summary_prompt)
#             feedback_response = await llm.ainvoke(feedback_prompt)

#             # 피드백 생성
#             communication_summary = _clean_text(summary_response.content)
#             feedback_summary = _clean_text(feedback_response.content)
#         except Exception as e:
#             logger.error(f"AI 요약/피드백 생성 실패: {str(e)}")
#             # 생성 실패 시 기본 메시지 사용

#         report_request = CreateReportRequest(
#             memberId=member_id,
#             callId=call_id,
#             callDuration=duration,
#             celebVideoUrl=None,
#             wordCount=word_count,
#             sentenceCount=sentence_count,
#             communicationSummary=communication_summary,
#             feedbackSummary=feedback_summary,
#         )

#         # 리포트 생성
#         new_report = await report.create_report(db=db, request=report_request)

#         # 원어민 표현 생성 및 저장 (직접 호출로 변경)
#         await generate_and_save_native_expressions(
#             db=db, call_id=call_id, report_id=new_report.reportId
#         )
#     except Exception as e:
#         logger.error(f"Failed to create report or native expressions: {str(e)}")
#         # 리포트 생성 실패는 통화 종료에 영향을 주지 않도록 예외를 전파하지 않음


# def _clean_text(text: str) -> str:
#     """
#     텍스트 길이 제한 및 특수 문자 처리
#     """
#     # 줄바꿈, 따옴표, 기타 문제가 될 수 있는 문자 제거
#     # 큰따옴표를 작은따옴표로 변경
#     # 백슬래시 제거
#     # 500자로 길이 제한
#     return (
#         text.replace("\n", " ").replace("\r", " ").replace('"', "'").replace("\\", "")
#     )[:500]


# app/service/report.py
import re
import logging
from app.crud.report import create_report
from app.graph import report_graph
from app.schema.call import Message
from app.schema.report import CreateReportRequest
from app.service.native_expression import save_native_expressions


logger = logging.getLogger(__name__)

create_report_graph = report_graph.build_report_graph()


async def generate_report(
    db,
    member_id: int,
    call_id: int,
    duration: int,
    messages: list[Message],
):
    try:
        # 상태 구성
        state = {
            "member_id": member_id,
            "call_id": call_id,
            "duration": duration,
            "messages": messages,
        }

        # LangGraph 실행
        result = await create_report_graph.ainvoke(state)

        # 사용자 메시지 기반 단어 수
        word_count = sum(
            len(m.content.split()) for m in messages if m.type == "human" and m.content
        )

        # 사용자 메시지 기반 문장 수 ('.', '!', '?' 기준으로 분리)
        sentence_count = sum(
            len(re.findall(r"[.!?]", m.content))
            for m in messages
            if m.type == "human" and m.content
        )

        # 결과 저장 처리
        await save_report_result(db, result, word_count, sentence_count)

    except Exception as e:
        logger.error(f"📉 리포트 생성 실패: {e}")


async def save_report_result(
    db, state: dict, word_count: int = 0, sentence_count: int = 0
):
    report_request = CreateReportRequest(
        memberId=state["member_id"],
        callId=state["call_id"],
        callDuration=state["duration"],
        celebVideoUrl=None,
        wordCount=word_count,
        sentenceCount=sentence_count,
        communicationSummary=state.get("summary", "요약 없음"),
        feedbackSummary=state.get("feedback", "피드백 없음"),
    )

    # DB에 리포트 저장
    new_report = await create_report(db=db, request=report_request)

    # 원어민 표현 DB 저장 - 새로운 데이터베이스 세션을 사용하여 충돌 방지
    native_expressions = state.get("native_expressions", [])
    await save_native_expressions(
        db=db, report_id=new_report.reportId, expressions=native_expressions
    )
