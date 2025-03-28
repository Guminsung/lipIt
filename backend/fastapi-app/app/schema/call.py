from pydantic import BaseModel, HttpUrl
from datetime import datetime
from typing import Optional


# 대화 메시지 형식
class Message(BaseModel):
    type: str  # "human" 또는 "ai"
    content: str  # 메시지 내용
    content_kor: Optional[str] = None  # 한글 번역
    audio_url: Optional[str] = None  # 음성 파일 URL
    timestamp: str  # 생성 시간


# 대화 시작 요청 DTO
class StartCallRequest(BaseModel):
    callRequestId: int
    memberId: int
    voiceId: int
    voiceAudioUrl: str
    topic: str


# 대화 시작 응답 DTO
class StartCallResponse(BaseModel):
    callId: int
    startTime: datetime
    aiMessage: str
    aiMessageKor: Optional[str] = None
    aiAudioUrl: str


# 사용자 메시지 요청 DTO
class UserMessageRequest(BaseModel):
    userMessage: str
    userMessageKor: Optional[str] = None


# AI 메시지 응답 DTO
class AIMessageResponse(BaseModel):
    aiMessage: str
    aiMessageKor: str
    aiAudioUrl: str


# 대화 종료 요청 DTO
class EndCallRequest(BaseModel):
    userMessage: str
    userMessageKor: Optional[str] = None
    endReason: str  # 예: "USER_REQUEST", "TIMEOUT", "AI_DECISION"


# 대화 종료 응답 DTO
class EndCallResponse(AIMessageResponse):  # 상속
    endTime: datetime
    duration: int
