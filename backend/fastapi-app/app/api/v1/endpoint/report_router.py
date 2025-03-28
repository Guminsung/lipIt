from fastapi import APIRouter, Query, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from app.api.v1.endpoint.report import get_reports_endpoint, get_report_summary_endpoint, get_report_script_endpoint, get_report_expressions_endpoint
from app.schema.report import ReportResponse, ReportSummaryResponse, ReportScriptResponse, ReportExpressionsResponse
from app.schema.common import APIResponse
from app.exception.error_code import ErrorCode
from app.db.session import get_db
from typing import List
from app.core.base_router import BaseRouter

router = BaseRouter(prefix="/api/reports", tags=["Report"])

router.api_doc(
    path="",
    endpoint=get_reports_endpoint,
    request_model=None,
    response_model=APIResponse[List[ReportResponse]],
    success_model=APIResponse[List[ReportResponse]],
    errors={
        404: {
            "message": "요청하신 리포트를 찾을 수 없습니다.",
            "code": ErrorCode.REPORT_NOT_FOUND
        },
        500: {
            "message": "서버 내부 오류가 발생했습니다. 관리자에게 문의하세요.",
            "code": ErrorCode.REPORT_INTERNAL_ERROR
        }
    },
    summary="📊 리포트 목록 조회",
    description="사용자 ID를 기반으로 해당 사용자의 모든 리포트 목록을 조회합니다."
)
async def get_reports(
    member_id: int = Query(..., description="회원 ID"),
    db: AsyncSession = Depends(get_db)
):
    return await get_reports_endpoint(member_id, db)


router.api_doc(
    path="/{report_id}/summary",
    endpoint=get_report_summary_endpoint,
    request_model=None,
    response_model=APIResponse[ReportSummaryResponse],
    success_model=APIResponse[ReportSummaryResponse],
    errors={
        400: {
            "message": "잘못된 요청입니다. 입력 값을 확인해주세요.",
            "code": ErrorCode.REPORT_INTERNAL_ERROR
        },
        401: {
            "message": "인증이 필요합니다. 로그인 후 다시 시도해주세요.",
            "code": ErrorCode.REPORT_INTERNAL_ERROR
        },
        403: {
            "message": "접근이 거부되었습니다. 권한을 확인해주세요.",
            "code": ErrorCode.REPORT_INTERNAL_ERROR
        },
        404: {
            "message": "요청하신 리포트를 찾을 수 없습니다.",
            "code": ErrorCode.REPORT_NOT_FOUND
        },
        500: {
            "message": "서버 내부 오류가 발생했습니다. 관리자에게 문의하세요.",
            "code": ErrorCode.REPORT_INTERNAL_ERROR
        }
    },
    summary="📝 리포트 요약 조회",
    description="특정 리포트의 요약 정보를 조회합니다."
)
async def get_report_summary(
    report_id: int,
    db: AsyncSession = Depends(get_db)
):
    return await get_report_summary_endpoint(report_id, db)


router.api_doc(
    path="/{report_id}/script",
    endpoint=get_report_script_endpoint,
    request_model=None,
    response_model=APIResponse[ReportScriptResponse],
    success_model=APIResponse[ReportScriptResponse],
    errors={
        400: {
            "message": "잘못된 요청입니다. 입력 값을 확인해주세요.",
            "code": ErrorCode.REPORT_INTERNAL_ERROR
        },
        401: {
            "message": "인증이 필요합니다. 로그인 후 다시 시도해주세요.",
            "code": ErrorCode.REPORT_INTERNAL_ERROR
        },
        403: {
            "message": "접근이 거부되었습니다. 권한을 확인해주세요.",
            "code": ErrorCode.REPORT_INTERNAL_ERROR
        },
        404: {
            "message": "요청하신 리포트를 찾을 수 없습니다.",
            "code": ErrorCode.REPORT_NOT_FOUND
        },
        500: {
            "message": "서버 내부 오류가 발생했습니다. 관리자에게 문의하세요.",
            "code": ErrorCode.REPORT_INTERNAL_ERROR
        }
    },
    summary="📝 리포트 스크립트 조회",
    description="특정 리포트의 통화 스크립트를 조회합니다."
)
async def get_report_script(
    report_id: int,
    db: AsyncSession = Depends(get_db)
):
    return await get_report_script_endpoint(report_id, db)


router.api_doc(
    path="/{report_id}/expressions",
    endpoint=get_report_expressions_endpoint,
    request_model=None,
    response_model=APIResponse[ReportExpressionsResponse],
    success_model=APIResponse[ReportExpressionsResponse],
    errors={
        400: {
            "message": "잘못된 요청입니다. 입력 값을 확인해주세요.",
            "code": ErrorCode.REPORT_INTERNAL_ERROR
        },
        401: {
            "message": "인증이 필요합니다. 로그인 후 다시 시도해주세요.",
            "code": ErrorCode.REPORT_INTERNAL_ERROR
        },
        403: {
            "message": "접근이 거부되었습니다. 권한을 확인해주세요.",
            "code": ErrorCode.REPORT_INTERNAL_ERROR
        },
        404: {
            "message": "요청하신 리포트를 찾을 수 없습니다.",
            "code": ErrorCode.REPORT_NOT_FOUND
        },
        500: {
            "message": "서버 내부 오류가 발생했습니다. 관리자에게 문의하세요.",
            "code": ErrorCode.REPORT_INTERNAL_ERROR
        }
    },
    summary="🗣️ 리포트 원어민 표현 조회",
    description="리포트에 포함된 사용자 대화를 기반으로 원어민 표현을 추천합니다."
)
async def get_report_expressions(
    report_id: int,
    db: AsyncSession = Depends(get_db)
):
    return await get_report_expressions_endpoint(report_id, db) 