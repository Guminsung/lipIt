# app/graph/pinecone_client.py
from pinecone import Pinecone, ServerlessSpec

from app.core.config import INDEX_NAME, PINECONE_API_KEY


pc = Pinecone(api_key=PINECONE_API_KEY)


def get_index():
    if INDEX_NAME not in pc.list_indexes().names():
        pc.create_index(
            name=INDEX_NAME,
            dimension=384,  # BGE-small 모델의 출력 차원
            metric="cosine",
            spec=ServerlessSpec(cloud="aws", region="us-east-1"),
        )
    return pc.Index(INDEX_NAME)
