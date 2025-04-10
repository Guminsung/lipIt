from fastapi import Request
from fastapi.responses import JSONResponse
from app.exception.custom_exceptions import APIException


async def api_exception_handler(request: Request, exc: APIException):
    return JSONResponse(
        status_code=exc.status_code,
        content=exc.detail,  # {"status", "message", "errorCode"} 형태
    )
