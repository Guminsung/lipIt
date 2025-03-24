from app.rag.search import search_relevant_call_memory
from app.schema.common import APIResponse
from app.schema.rag import RAGTestRequest, RAGTestResponse


async def test_rag_memory_search(request: RAGTestRequest):
    results = await search_relevant_call_memory(
        user_id=request.userId, query=request.query
    )
    return APIResponse(
        status=200,
        message="검색에 성공했습니다.",
        data=RAGTestResponse(relatedCalls=results),
    )
