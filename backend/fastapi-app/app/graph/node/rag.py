# app/graph/node/rag.py
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
    filtered = [
        {"content": m["content"], "score": m["score"]}
        for m in matches
        if m["score"] >= 0.5
    ][:3]

    state["retrieved_context"] = filtered
    return state

    # member_id = state["member_id"]
    # user_input = state["input"]

    # matches = await search_relevant_call_memory(
    #     member_id=member_id, query=user_input, top_k=10
    # )
    # raw_texts = [m["content"] for m in matches if m.get("score", 0) >= 0.5]

    # # 1. chunking
    # chunked = chunk_contexts(raw_texts)  # 줄 단위 분리
    # deduped = list(set(chunked))  # 중복 제거

    # # 2. 요약 (유사 문장 압축)
    # summary_contexts = await summarize_contexts_by_embedding(deduped)

    # # 3. 결과 저장
    # state["retrieved_context"] = raw_texts
    # return state
