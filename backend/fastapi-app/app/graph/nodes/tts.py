# app/graph/nodes/tts.py
from datetime import datetime

from app.util.datetime_utils import now_kst

S3_BASE_URL = "https://s3.amazonaws.com/ai_audio/"


async def tts_node(state: dict) -> dict:
    """
    텍스트 응답을 받아 임의의 오디오 URL 생성 (실제 TTS 대신 mock 처리)
    """
    audio_file_id = f"response_{int(now_kst().timestamp())}.mp3"
    state["ai_audio_url"] = f"{S3_BASE_URL}{audio_file_id}"
    return state
