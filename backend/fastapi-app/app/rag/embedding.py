# app/graph/embedding.py
from langchain_community.embeddings import HuggingFaceEmbeddings


def get_embedding_model():
    return HuggingFaceEmbeddings(
        model_name="BAAI/bge-small-en-v1.5",
        model_kwargs={"device": "cpu"},
    )


def get_embedding(text: str) -> list:
    """텍스트 임베딩 생성 (HuggingFace BGE 모델 기반)"""
    model = get_embedding_model()
    return model.embed_query(text)
