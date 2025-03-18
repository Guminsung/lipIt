from fastapi import APIRouter
from app.api.v1.endpoints import call, message  # 필요한 엔드포인트 import

router = APIRouter()

# 엔드포인트 라우터 등록
router.include_router(call.router, tags=["Calls"])
router.include_router(message.router, tags=["Messages"])
