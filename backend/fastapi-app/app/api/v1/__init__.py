from app.api.v1.endpoint.call_router import router as call_router
from app.api.v1.endpoint.rag_router import router as rag_router


def get_routers():
    return [call_router, rag_router]
