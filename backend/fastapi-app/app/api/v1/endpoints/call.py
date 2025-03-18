from fastapi import APIRouter, HTTPException, Path
from app.schemas.call import CallCreate, CallResponse, CallEndRequest, CallEndResponse
from app.crud.call import create_call, end_call

router = APIRouter()


# 대화 시작 API
@router.post("/calls", response_model=CallResponse)
async def start_call(request: CallCreate):
    try:
        call_data = await create_call(
            request.userId, request.voiceId, request.voiceAudioUrl, request.topic
        )
        return call_data
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


# 대화 종료 API
@router.post("/calls/{callId}", response_model=CallEndResponse)
async def terminate_call(
    callId: str = Path(..., description="대화 ID"), request: CallEndRequest = None
):
    result = await end_call(callId, request.userResponse, request.endReason)

    if result is None:
        raise HTTPException(
            status_code=404,
            detail="해당 통화 기록을 찾을 수 없습니다.",
            headers={"ErrorCode": "CALL-001"},
        )

    return result
