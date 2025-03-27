# app/graph/store.py
import asyncio
from app.rag.embedding import get_embedding
from app.rag.pinecone_client import get_index
import logging

logger = logging.getLogger(__name__)


async def store_call_history_embedding(call_id, member_id, messages):
    """통화 메시지 리스트를 하나의 텍스트로 묶어 벡터 저장 + 원문도 저장"""
    joined_text = "\n".join([f"{m.type}: {m.content}" for m in messages])
    embedding = await get_embedding(joined_text)

    # 동기 함수는 executor에 위임할 수도 있음
    await asyncio.to_thread(
        lambda: get_index().upsert(
            [
                {
                    "id": f"call-{call_id}",
                    "values": embedding,
                    "metadata": {
                        "member_id": str(member_id),
                        "content": joined_text,
                    },
                }
            ]
        )
    )
