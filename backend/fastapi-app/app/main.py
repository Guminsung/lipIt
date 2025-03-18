from fastapi import FastAPI
from app.db.session import mongodb
from app.api.v1.routers import router as api_router  # 통합된 라우터 가져오기

app = FastAPI()


@app.on_event("startup")
async def startup():
    await mongodb.connect()


@app.on_event("shutdown")
async def shutdown():
    await mongodb.close()


# `routers.py`에서 통합한 라우터를 등록
app.include_router(api_router, prefix="/api")


@app.get("/")
async def root():
    return {"message": "FastAPI & MongoDB Call API"}
