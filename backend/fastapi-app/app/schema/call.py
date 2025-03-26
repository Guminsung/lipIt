from pydantic import BaseModel, HttpUrl
from datetime import datetime
from typing import Optional


# 메시지 형식
class Message(BaseModel):
    type: str  # "ai" 또는 "user"
    content: str  # 메시지 내용
    content_kor: Optional[str] = None  # 한글 번역
    audio_url: Optional[str] = None  # 음성 파일 URL
    timestamp: str  # 생성 시간


# 요청 DTO
class StartCallRequest(BaseModel):
    callRequestId: int
    memberId: int
    voiceId: int
    voiceAudioUrl: str
    topic: str


# 응답 DTO
class StartCallResponse(BaseModel):
    callId: int
    startTime: datetime
    aiFirstMessage: str


# 사용자 메시지 요청 DTO
class UserMessageRequest(BaseModel):
    userMessage: str
    userMessageKor: Optional[str] = None
    userAudioUrl: Optional[HttpUrl] = None


# AI 응답 DTO
class AIMessageResponse(BaseModel):
    aiMessage: str
    aiAudioUrl: Optional[HttpUrl] = None


class EndCallRequest(BaseModel):
    userMessage: str
    userMessageKor: Optional[str] = None
    endReason: str  # 예: "USER_REQUEST", "TIMEOUT", "AI_DECISION"


class EndCallResponse(BaseModel):
    callId: int
    endTime: datetime
    duration: int
    aiEndMessage: str
