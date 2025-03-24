import os
from dotenv import load_dotenv
from pinecone import Pinecone, ServerlessSpec


load_dotenv()
pc = Pinecone(api_key=os.getenv("PINECONE_API_KEY"))

INDEX_NAME = os.getenv("PINECONE_INDEX")


def get_index():
    if INDEX_NAME not in pc.list_indexes().names():
        pc.create_index(
            name=INDEX_NAME,
            dimension=384,  # BGE-small 모델의 출력 차원
            metric="cosine",
            spec=ServerlessSpec(cloud="aws", region="us-east-1"),
        )
    return pc.Index(INDEX_NAME)
