from app.db.session import mongodb
from datetime import datetime
from bson.objectid import ObjectId
from typing import Optional
import random


# AI 응답을 생성하는 함수 (임시)
def generate_ai_response(user_message: str) -> str:
    responses = [
        "I really enjoy basketball! The fast-paced action makes it so exciting to watch. How about you?",
        "Soccer is my favorite sport! The World Cup is always thrilling to watch.",
        "Tennis is amazing. Have you seen the latest Grand Slam match?",
        "Baseball has such a great history. Do you follow any teams?",
    ]
    return random.choice(responses)


# AI 음성 URL을 생성하는 함수 (임시)
def generate_ai_audio_url() -> str:
    return f"https://s3.amazonaws.com/ai_audio/response_{random.randint(100,999)}.mp3"


# 사용자 메시지를 저장하고 AI 응답을 반환하는 함수
async def add_message_to_call(
    call_id: str,
    user_message: str,
    user_message_kor: Optional[str],
    user_audio_url: Optional[str],
):
    # MongoDB에서 통화 기록 조회
    call = await mongodb.db["calls"].find_one({"_id": ObjectId(call_id)})

    if not call:
        return None  # 통화 기록 없음 (404 예외 처리 예정)

    # AI 응답 생성
    ai_response = generate_ai_response(user_message)
    ai_audio_url = generate_ai_audio_url()

    new_messages = [
        {
            "role": "user",
            "text": user_message,
            "text_kor": user_message_kor,
            "audio_url": user_audio_url,
            "timestamp": datetime.utcnow(),
        },
        {
            "role": "ai",
            "text": ai_response,
            "text_kor": None,
            "audio_url": ai_audio_url,
            "timestamp": datetime.utcnow(),
        },
    ]

    # MongoDB에 메시지 추가
    await mongodb.db["calls"].update_one(
        {"_id": ObjectId(call_id)}, {"$push": {"messages": {"$each": new_messages}}}
    )

    return {"aiMessage": ai_response, "aiAudioUrl": ai_audio_url}
