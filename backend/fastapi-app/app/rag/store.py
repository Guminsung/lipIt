# app/graph/store.py
from typing import List
from app.schema.call import Message
from app.rag.embedding import get_embedding
from app.rag.pinecone_client import get_index


async def store_call_history_embedding(
    call_id: int, member_id: int, messages: List[Message]
):
    """통화 메시지 리스트를 하나의 텍스트로 묶어 벡터 저장 + 원문도 저장"""
    joined_text = "\n".join([f"{m.type}: {m.content}" for m in messages])
    embedding = get_embedding(joined_text)

    index = get_index()
    index.upsert(
        [
            {
                "id": f"call-{call_id}",
                "values": embedding,
                "metadata": {
                    "member_id": str(member_id),
                    "call_id": str(call_id),
                    "content": joined_text,  # 검색 후 원문 활용 가능
                },
            }
        ]
    )
