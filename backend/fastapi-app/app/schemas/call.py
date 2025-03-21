from pydantic import BaseModel, HttpUrl
from datetime import datetime
from typing import Generic, Optional, TypeVar, List

# Generic을 위한 타입 변수 선언
T = TypeVar("T")


# 공통 API 응답 형식 (Generic 적용)
class APIResponse(BaseModel, Generic[T]):
    status: int
    message: str
    data: Optional[T] = None


# AI의 첫 메시지 형식
class Message(BaseModel):
    role: str  # "ai" 또는 "user"
    text: str  # 메시지 내용
    text_kor: Optional[str] = None  # 한글 번역
    audio_url: Optional[str] = None  # 음성 파일 URL
    timestamp: int  # 생성 시간


# 요청 DTO
class CallRequest(BaseModel):
    userId: int
    voiceId: int
    voiceAudioUrl: str
    topic: str


# 응답 DTO
class CallResponse(BaseModel):
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
