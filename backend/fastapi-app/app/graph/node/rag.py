# app/graph/nodes/rag.py
from app.rag.search import search_relevant_call_memory


async def rag_node(state: dict) -> dict:
    """
    Pinecone에서 과거 메시지를 벡터 기반으로 검색
    """
    member_id = state["member_id"]
    user_input = state["input"]

    matches = await search_relevant_call_memory(
        member_id=member_id, query=user_input, top_k=3
    )

    # 유사도 필터링 및 상위 3개 선택
    filtered = [m["content"] for m in matches if m["score"] >= 0.5][:3]
    state["retrieved_context"] = filtered
    return state
