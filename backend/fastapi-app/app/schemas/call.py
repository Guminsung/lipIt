# CallBase가 없으면 아래처럼 추가해줘
from pydantic import BaseModel
from datetime import datetime
from typing import Optional, List
from app.schemas.message import Message


class CallBase(BaseModel):  # CallBase 정의 추가
    userId: int
    voiceId: int
    voiceAudioUrl: Optional[str] = None
    topic: str
    startTime: datetime
    messages: List[Message] = []


# 대화 시작 요청 DTO
class CallCreate(BaseModel):
    userId: int
    voiceId: int
    voiceAudioUrl: Optional[str] = None  # 커스텀 음성일 경우 S3 URL 저장
    topic: str


# 대화 시작 응답 DTO
class CallResponse(BaseModel):
    callId: str
    startTime: datetime
    aiFirstMessage: str
    messages: List[Message] = []


# 대화 종료 요청 DTO
class CallEndRequest(BaseModel):
    userResponse: str
    endReason: str  # 종료 사유 (USER_REQUEST, TIMEOUT, AI_DECISION)


# 대화 종료 응답 DTO
class CallEndResponse(BaseModel):
    callId: str
    endTime: datetime
    duration: int
    aiEndMessage: str
