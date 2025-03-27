# app/api/v1/endpoint/audio_router.py:
from app.api.v1.endpoint.audio import upload_file
from app.core.base_router import BaseRouter
from app.schema.common import APIResponse
from app.exception.error_code import Error
from pydantic import BaseModel


class AudioUploadResponse(BaseModel):
    url: str


router = BaseRouter(prefix="/api/audio", tags=["Audio"])


router.api_doc(
    path="/upload",
    endpoint=upload_file,
    methods=["POST"],
    request_model=None,
    response_model=APIResponse[AudioUploadResponse],
    success_model=AudioUploadResponse,
    success_example={"url": "https://dlxayir1dj7sa.cloudfront.net/test/abc123.mp4"},
    errors={
        500: {
            "message": Error.AUDIO_UPLOAD_ERROR.message,
            "code": Error.AUDIO_UPLOAD_ERROR.code,
        }
    },
    summary="🔼 음성 파일 업로드",
    description="사용자가 업로드한 음성 파일(wav, mp4 등)을 S3에 저장하고, CloudFront로 접근 가능한 URL을 반환합니다.",
)
