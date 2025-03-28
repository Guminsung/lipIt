from app.api.v1.endpoint.audio_router import router as audio_router
from app.api.v1.endpoint.call_router import router as call_router
from app.api.v1.endpoint.rag_router import router as rag_router
from app.api.v1.endpoint.report_router import router as report_router


def get_routers():
    return [call_router, rag_router, audio_router, report_router]
