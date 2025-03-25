# rag/search.py
from typing import List
from app.rag.embedding import get_embedding
from app.rag.pinecone_client import get_index


async def search_relevant_call_memory(
    member_id: int, query: str, top_k: int = 3
) -> List[dict]:
    embedding = get_embedding(query)
    index = get_index()

    result = index.query(
        vector=embedding,
        top_k=top_k,
        include_metadata=True,
        filter={"member_id": str(member_id)},
    )

    return [
        {
            "callId": match.metadata.get("call_id"),
            "content": match.metadata.get("content", ""),
            "score": match.score,
        }
        for match in result.matches
    ]
