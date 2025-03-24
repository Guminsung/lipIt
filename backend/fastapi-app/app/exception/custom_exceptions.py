# app/exception/custom_exceptions.py

from fastapi import HTTPException
from starlette.status import HTTP_400_BAD_REQUEST


from fastapi import HTTPException


class APIException(HTTPException):
    def __init__(self, status_code: int, message: str, error_code: str):
        super().__init__(
            status_code=status_code,
            detail={"status": status_code, "message": message, "errorCode": error_code},
        )
