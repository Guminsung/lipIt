# app/crud/audio.py
from io import BytesIO
from uuid import uuid4
from fastapi import UploadFile
import boto3

from app.core.config import (
    AWS_ACCESS_KEY_ID,
    AWS_SECRET_ACCESS_KEY,
    AWS_REGION,
    S3_BUCKET_NAME,
    CLOUDFRONT_DOMAIN,
)


s3 = boto3.client(
    "s3",
    aws_access_key_id=AWS_ACCESS_KEY_ID,
    aws_secret_access_key=AWS_SECRET_ACCESS_KEY,
    region_name=AWS_REGION,
)


def upload_file_to_s3(file: UploadFile) -> str:
    try:
        fileName = file.filename.split(".")[0]
        ext = file.filename.split(".")[-1]
        key = f"test/{fileName}.{ext}"

        s3.upload_fileobj(
            file.file, S3_BUCKET_NAME, key, ExtraArgs={"ContentType": file.content_type}
        )

        return f"https://{CLOUDFRONT_DOMAIN}/{key}"
    except Exception as e:
        # 필요 시 로깅 추가 가능
        raise RuntimeError(f"S3 업로드 실패: {str(e)}")


def upload_bytes_to_s3(data: bytes, content_type: str = "audio/mpeg") -> str:
    key = f"audio/{uuid4()}.mp3"

    s3.upload_fileobj(
        Fileobj=BytesIO(data),
        Bucket=S3_BUCKET_NAME,
        Key=key,
        ExtraArgs={"ContentType": content_type},
    )

    return f"https://{CLOUDFRONT_DOMAIN}/{key}"
