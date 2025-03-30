# app/service/report.py
import re
import logging
from app.crud.report import create_report
from app.graph import report_graph
from app.rag.store import store_meaningful_messages
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

        # 의미 있는 메시지 + 태그 벡터 DB 저장
        meaningful_messages = result.get("meaningful_messages", [])

        print(f"⭐ meaningful_messages = {meaningful_messages}")

        if meaningful_messages:
            await store_meaningful_messages(
                call_id=call_id,
                member_id=member_id,
                messages=meaningful_messages,
            )

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
