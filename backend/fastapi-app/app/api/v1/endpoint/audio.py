# app/api/v1/endpoint/audio_router.py
from fastapi import UploadFile, File
from app.core.base_router import BaseRouter
from app.crud.audio import upload_file_to_s3
from app.schema.common import APIResponse
from app.exception.error_code import Error
from pydantic import BaseModel


class AudioUploadResponse(BaseModel):
    url: str


router = BaseRouter(prefix="/api/audio", tags=["Audio"])


async def upload_file(file: UploadFile = File(...)):
    try:
        url = upload_file_to_s3(file)
        return APIResponse(
            status=200,
            message="파일 업로드에 성공했습니다.",
            data=AudioUploadResponse(url=url),
        )
    except Exception as e:
        raise APIResponse(500, Error.AUDIO_UPLOAD_ERROR)
