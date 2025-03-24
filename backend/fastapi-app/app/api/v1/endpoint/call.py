from fastapi import Depends
from sqlalchemy.ext.asyncio import AsyncSession

from app.db.session import get_db
from app.schema.common import APIResponse
from app.schema.call import (
    StartCallRequest,
    UserMessageRequest,
    EndCallRequest,
)
from app.crud.call import create_call, add_message_to_call, end_call
from app.exception.custom_exceptions import APIException
from app.exception.error_code import ErrorCode


# 1. 통화 시작
async def create_call_endpoint(
    request: StartCallRequest, db: AsyncSession = Depends(get_db)
):
    try:
        call_response = await create_call(db, request)
        return APIResponse(
            status=200, message="대화가 성공적으로 시작되었습니다.", data=call_response
        )
    except Exception:
        raise APIException(
            500, "통화 시작 중 서버 오류가 발생했습니다.", ErrorCode.CALL_INTERNAL_ERROR
        )


# 2. 메시지 추가
async def add_message_to_call_endpoint(
    callId: int, request: UserMessageRequest, db: AsyncSession = Depends(get_db)
):
    try:
        response_data = await add_message_to_call(db, callId, request)
        return APIResponse(
            status=200, message="대화가 성공적으로 진행되었습니다.", data=response_data
        )
    except Exception as e:
        if "해당 통화 기록을 찾을 수 없습니다." in str(e):
            raise APIException(
                404, "해당 통화 기록을 찾을 수 없습니다.", ErrorCode.CALL_NOT_FOUND
            )
        raise APIException(
            500,
            "메시지 처리 중 서버 오류가 발생했습니다.",
            ErrorCode.CALL_INTERNAL_ERROR,
        )


# 3. 통화 종료
async def end_call_endpoint(
    callId: int, request: EndCallRequest, db: AsyncSession = Depends(get_db)
):
    try:
        response_data = await end_call(db, callId, request)
        return APIResponse(
            status=200, message="대화가 성공적으로 종료되었습니다.", data=response_data
        )
    except Exception as e:
        if "해당 통화 기록을 찾을 수 없습니다." in str(e):
            raise APIException(
                404, "해당 통화 기록을 찾을 수 없습니다.", ErrorCode.CALL_NOT_FOUND
            )
        if "이미 종료된 통화입니다." in str(e):
            raise APIException(
                400, "이미 종료된 통화입니다.", ErrorCode.CALL_ALREADY_ENDED
            )
        raise APIException(
            500, "통화 종료 중 서버 오류가 발생했습니다.", ErrorCode.CALL_INTERNAL_ERROR
        )
