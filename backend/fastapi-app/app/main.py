from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse
from contextlib import asynccontextmanager
from app.api.v1 import get_routers
from app.db.session import init_db
from app.exception.custom_exceptions import APIException
from app.util.docs.error_code_reference import get_error_code_reference

import logging

logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
    """애플리케이션 시작 시 DB 테이블 생성"""
    await init_db()
    yield


app = FastAPI(
    root_path="/fastapi",
    debug=True,
    lifespan=lifespan,
    title="[Lip It] REST API",
    description="SSAFY AI 영상 도메인 특화 프로젝트 **Lip It 서비스의 API 명세서**입니다."
    + get_error_code_reference(),
    version="1.0.0",
)

# 라우터 등록
for router in get_routers():
    app.include_router(router)


@app.exception_handler(APIException)
async def api_exception_handler(request: Request, exc: APIException):
    logger.error(f"APIException - {exc.status_code}: {exc.detail}", exc_info=True)
    return JSONResponse(status_code=exc.status_code, content=exc.detail)


if __name__ == "__main__":
    import uvicorn

    uvicorn.run("app.main:app", host="0.0.0.0", port=8000, reload=True)
