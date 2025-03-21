from fastapi import FastAPI
from contextlib import asynccontextmanager
from app.db.session import init_db
from app.api.v1.routers import router as calls_router


@asynccontextmanager
async def lifespan(app: FastAPI):
    """애플리케이션 시작 시 DB 테이블 생성"""
    await init_db()
    yield


app = FastAPI(lifespan=lifespan)

# 라우터 등록
app.include_router(calls_router)


@app.get("/")
async def root():
    return {"message": "FastAPI & PostgreSQL API"}
