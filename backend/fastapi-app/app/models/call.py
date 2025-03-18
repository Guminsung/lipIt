from typing import Optional, List
from pydantic import BaseModel
from datetime import datetime


class Message(BaseModel):
    role: str  # "ai" 또는 "user"
    text: str
    text_kor: Optional[str] = None
    audio_url: Optional[str] = None
    timestamp: datetime


class CallBase(BaseModel):
    user_id: int
    call_id: int
    ai_voice_id: int
    start_time: datetime
    end_time: Optional[datetime] = None
    duration: Optional[int] = None
    messages: List[Message] = []


class CallCreate(CallBase):
    pass


class CallResponse(CallBase):
    id: str
