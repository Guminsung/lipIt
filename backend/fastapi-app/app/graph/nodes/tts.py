# app/graph/nodes/tts.py
from app.util.datetime_utils import now_kst
from google.cloud import texttospeech
from app.crud.audio import upload_bytes_to_s3


async def tts_node(state: dict) -> dict:
    text = state.get("ai_response", "기본 응답입니다.")

    client = texttospeech.TextToSpeechClient()

    synthesis_input = texttospeech.SynthesisInput(text=text)

    voice = texttospeech.VoiceSelectionParams(
        language_code="en-US",
        ssml_gender=texttospeech.SsmlVoiceGender.NEUTRAL,
    )

    audio_config = texttospeech.AudioConfig(
        audio_encoding=texttospeech.AudioEncoding.MP3
    )

    response = client.synthesize_speech(
        input=synthesis_input, voice=voice, audio_config=audio_config
    )

    # S3 업로드
    audio_url = upload_bytes_to_s3(response.audio_content, content_type="audio/mpeg")

    state["ai_audio_url"] = audio_url
    return state
