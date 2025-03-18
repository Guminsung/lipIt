from pydantic import BaseModel
from datetime import datetime
from typing import Optional


class Message(BaseModel):
    role: str  # "ai" 또는 "user"
    text: str
    text_kor: Optional[str] = None
    audio_url: Optional[str] = None
    timestamp: datetime


# ✅ 사용자 메시지 요청 DTO
class MessageCreate(BaseModel):
    userMessage: str
    userMessageKor: Optional[str] = None
    userAudioUrl: Optional[str] = None


# ✅ AI 응답 DTO
class MessageResponse(BaseModel):
    aiMessage: str
    aiAudioUrl: str
