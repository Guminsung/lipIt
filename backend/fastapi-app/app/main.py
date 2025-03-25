from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse
from contextlib import asynccontextmanager
import asyncio
from app.api.v1 import get_routers
from app.db.session import init_db
from app.exception.custom_exceptions import APIException
from app.util.docs.error_code_reference import get_error_code_reference
from app.core.scheduler import init_scheduler, shutdown_scheduler, crawl_news_job, crawl_weather_job
from app.core.logging import setup_logging

import logging

logger = logging.getLogger(__name__)

@asynccontextmanager
async def lifespan(app: FastAPI):
    """애플리케이션 시작 시 DB 테이블 생성"""
    # 로깅 설정
    setup_logging()
    logger.info("서버 시작 중...")
    
    # DB 초기화
    await init_db()
    
    # 초기 크롤링 실행
    try:
        await crawl_news_job()
        logger.info("초기 뉴스 크롤링 완료")
        
        # 날씨 크롤링 추가
        await crawl_weather_job()
        logger.info("초기 날씨 크롤링 완료")
    except Exception as e:
        logger.error(f"초기 크롤링 오류: {str(e)}")
    
    # 스케줄러 초기화
    init_scheduler()
    
    yield
    
    # 서버 종료 시 스케줄러 종료
    shutdown_scheduler()

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
    return JSONResponse(status_code=exc.status_code, content=exc.detail)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("app.main:app", host="0.0.0.0", port=8000, reload=True)
