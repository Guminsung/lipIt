# app/graph/node/rag.py
from app.rag.search import search_relevant_call_memory


async def rag_node(state: dict) -> dict:
    member_id = state["member_id"]
    user_input = state["input"]

    matches = await search_relevant_call_memory(
        member_id=member_id, query=user_input, top_k=5
    )

    # 유사도 필터링
    filtered = []
    for m in matches:
        if m["score"] >= 0.5:
            item = {
                "content": m["content"],
                "score": m["score"],
            }
            # summary_facts가 있다면 context로 활용할 수 있도록 추가
            if "summary_facts" in m:
                item["summary_facts"] = m["summary_facts"]
            filtered.append(item)

    state["retrieved_context"] = filtered[:3]
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
