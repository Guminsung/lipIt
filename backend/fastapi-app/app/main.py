# app/main.py
from fastapi import FastAPI
from app.routers import calls

app = FastAPI()

# 라우터 등록
app.include_router(calls.router, prefix="/calls", tags=["Calls"])


@app.get("/")
def root():
    return {"message": "Welcome to FastAPI!"}
