from app.db.session import mongodb
from datetime import datetime
from bson.objectid import ObjectId
from typing import Optional
import random


# AI의 첫 메시지를 생성하는 함수 (임시)
def generate_ai_first_message(topic: str) -> str:
    topic_messages = {
        "SPORTS": "Did you watch the latest NBA game? That last-minute three-pointer was insane!",
        "MUSIC": "Have you heard the latest album from Taylor Swift? It's amazing!",
        "MOVIES": "What's the last movie you watched? Did you enjoy it?",
    }
    return topic_messages.get(topic, "Let's talk about something interesting!")


# 통화 생성 함수
async def create_call(
    user_id: int, voice_id: int, voice_audio_url: Optional[str], topic: str
):
    start_time = datetime.utcnow()
    ai_first_message = generate_ai_first_message(topic)

    call_data = {
        "user_id": user_id,
        "voice_id": voice_id,
        "voice_audio_url": voice_audio_url,  # 커스텀 음성 URL 저장
        "topic": topic,
        "start_time": start_time,
        "end_time": None,
        "duration": None,
        "messages": [{"role": "ai", "text": ai_first_message, "timestamp": start_time}],
    }

    result = await mongodb.db["calls"].insert_one(call_data)
    return {
        "callId": str(result.inserted_id),
        "startTime": start_time,
        "aiFirstMessage": ai_first_message,
    }


# AI의 종료 메시지를 생성하는 함수 (임시)
def generate_ai_end_message(end_reason: str) -> str:
    end_messages = {
        "USER_REQUEST": "It was great talking with you today! Have a wonderful day!",
        "TIMEOUT": "Looks like we got disconnected. Hope to chat again soon!",
        "AI_DECISION": "Our conversation has come to a natural end. See you next time!",
    }
    return end_messages.get(end_reason, "Goodbye! Talk to you soon!")


# 대화 종료 처리 함수 (사용자 응답도 저장)
async def end_call(call_id: str, user_response: str, end_reason: str):
    # MongoDB에서 통화 기록 조회
    call = await mongodb.db["calls"].find_one({"_id": ObjectId(call_id)})

    if not call:
        return None  # 통화 기록 없음 (404 예외 처리 예정)

    # 현재 시간 기준으로 종료 시간 및 지속 시간 계산
    end_time = datetime.utcnow()
    start_time = call.get("start_time")
    duration = (end_time - start_time).seconds if start_time else 0

    # AI 종료 메시지 생성
    ai_end_message = generate_ai_end_message(end_reason)

    # 사용자 메시지와 AI 종료 메시지를 추가
    new_messages = [
        {"role": "user", "text": user_response, "timestamp": end_time},
        {"role": "ai", "text": ai_end_message, "timestamp": end_time},
    ]

    # MongoDB에 종료 정보 업데이트
    await mongodb.db["calls"].update_one(
        {"_id": ObjectId(call_id)},
        {
            "$set": {"end_time": end_time, "duration": duration},
            "$push": {"messages": {"$each": new_messages}},
        },
    )

    return {
        "callId": call_id,
        "endTime": end_time,
        "duration": duration,
        "aiEndMessage": ai_end_message,
    }
