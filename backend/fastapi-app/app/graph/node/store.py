# app/graph/nodes/store.py
from app.rag.store import store_call_history_embedding


async def store_node(state: dict) -> dict:
    """
    전체 통화 메시지를 벡터로 변환 후 Pinecone에 저장
    """
    await store_call_history_embedding(
        call_id=state["call_id"],
        member_id=state["member_id"],
        messages=state["messages"],
    )
    return state
