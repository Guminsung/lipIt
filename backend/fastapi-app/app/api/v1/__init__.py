from app.api.v1.endpoint.call_route import router as call_router
from app.api.v1.endpoint.rag_route import router as rag_router


def get_routers():
    return [call_router, rag_router]
