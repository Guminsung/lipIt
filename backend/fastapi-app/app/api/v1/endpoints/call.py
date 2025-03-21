from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession
from app.db.session import get_db
from app.schemas.call import (
    AIMessageResponse,
    CallRequest,
    APIResponse,
    CallResponse,
    UserMessageRequest,
)
from app.crud.call import add_message_to_call, create_call

router = APIRouter(prefix="/api/calls", tags=["Calls"])


@router.post("/", response_model=APIResponse[CallResponse])
async def create_call_endpoint(
    request: CallRequest, db: AsyncSession = Depends(get_db)
):
    """💡 사용자가 선택한 **AI 음성 ID와 대화 주제**를 기반으로 새로운 통화를 생성하고 **PostgreSQL에 정보(통화 시작 시간, AI 응답)를 저장**하고 AI의 첫 메시지를 반환"""
    try:
        call_response = await create_call(db, request)
        return APIResponse(
            status=200, message="대화가 성공적으로 시작되었습니다.", data=call_response
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.patch("/{callId}/messages", response_model=APIResponse[AIMessageResponse])
async def add_message_to_call_endpoint(
    callId: int, request: UserMessageRequest, db: AsyncSession = Depends(get_db)
):
    """💡 사용자 응답 내용과 이를 기반으로 생성된 AI 응답을 messages 리스트에 추가"""
    try:
        response_data = await add_message_to_call(db, callId, request)
        return APIResponse(
            status=200, message="대화가 성공적으로 진행되었습니다.", data=response_data
        )
    except Exception as e:
        if "해당 통화 기록을 찾을 수 없습니다." in str(e):
            raise HTTPException(status_code=404, detail="CALL-001")
        raise HTTPException(status_code=500, detail=str(e))
