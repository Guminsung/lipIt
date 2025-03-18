from fastapi import APIRouter, HTTPException, Path
from app.schemas.message import MessageCreate, MessageResponse
from app.crud.message import add_message_to_call

router = APIRouter()


# 대화 메시지 추가 API
@router.post("/calls/{callId}/messages", response_model=MessageResponse)
async def add_message(
    callId: str = Path(..., description="대화 ID"), request: MessageCreate = None
):
    result = await add_message_to_call(
        callId, request.userMessage, request.userMessageKor, request.userAudioUrl
    )

    if result is None:
        raise HTTPException(
            status_code=404,
            detail="해당 통화 기록을 찾을 수 없습니다.",
            headers={"ErrorCode": "CALL-001"},
        )

    return result
