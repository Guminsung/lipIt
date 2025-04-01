from fastapi import Depends
from sqlalchemy.ext.asyncio import AsyncSession

from app.db.session import get_db
from app.schema.common import APIResponse
from app.schema.call import StartCallRequest, UserMessageRequest
from app.service.call import start_call, add_message_to_call, end_call


# 통화 시작
async def start_call_endpoint(
    request: StartCallRequest, db: AsyncSession = Depends(get_db)
):
    call_response = await start_call(db, request)
    return APIResponse(
        status=200, message="대화가 성공적으로 시작되었습니다.", data=call_response
    )


# 메시지 추가
async def add_message_to_call_endpoint(
    callId: int, request: UserMessageRequest, db: AsyncSession = Depends(get_db)
):
    response_data = await add_message_to_call(db, callId, request)
    return APIResponse(
        status=200, message="대화가 성공적으로 진행되었습니다.", data=response_data
    )


# 통화 종료
async def end_call_endpoint(callId: int, db: AsyncSession = Depends(get_db)):
    response_data = await end_call(db, callId)
    return APIResponse(
        status=200, message="대화가 성공적으로 종료되었습니다.", data=response_data
    )
