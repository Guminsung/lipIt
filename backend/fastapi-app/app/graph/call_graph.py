# app/graph/call_graph.py
from langgraph.graph import StateGraph
from app.graph.nodes import (
    memory_node,
    start_prompt_node,
    end_prompt_node,
    rag_node,
    prompt_node,
    llm_node,
    tts_node,
    store_node,
)

from app.graph.state import CallState


# create_call용 그래프 (시작 프롬프트 + 응답 생성)
def build_create_call_graph():
    builder = StateGraph(CallState)

    builder.add_node("start_prompt", start_prompt_node)
    builder.add_node("llm", llm_node)
    builder.add_node("tts", tts_node)
    builder.add_node("memory", memory_node)

    builder.set_entry_point("start_prompt")
    builder.add_edge("start_prompt", "llm")
    builder.add_edge("llm", "tts")
    builder.add_edge("tts", "memory")
    builder.set_finish_point("memory")

    return builder.compile()


# add_message_to_call용 그래프 (RAG + 답변 + 음성)
def build_add_message_graph():
    builder = StateGraph(CallState)

    builder.add_node("memory", memory_node)
    builder.add_node("rag", rag_node)
    builder.add_node("prompt", prompt_node)
    builder.add_node("llm", llm_node)
    builder.add_node("tts", tts_node)
    builder.add_node("memory2", memory_node)  # 마지막 메시지 저장 전용

    builder.set_entry_point("memory")
    builder.add_edge("memory", "rag")
    builder.add_edge("rag", "prompt")
    builder.add_edge("prompt", "llm")
    builder.add_edge("llm", "tts")
    builder.add_edge("tts", "memory2")
    builder.set_finish_point("memory2")  # 메시지 저장 후 종료

    return builder.compile()


# end_call용 그래프 (종료 멘트 + 저장)
def build_end_call_graph():
    builder = StateGraph(CallState)

    builder.add_node("memory", memory_node)
    builder.add_node("end_prompt", end_prompt_node)
    builder.add_node("llm", llm_node)
    builder.add_node("tts", tts_node)
    builder.add_node("memory2", memory_node)
    # builder.add_node("store", store_node)

    builder.set_entry_point("memory")
    builder.add_edge("memory", "end_prompt")
    builder.add_edge("end_prompt", "llm")
    builder.add_edge("llm", "tts")
    builder.add_edge("tts", "memory2")
    # builder.add_edge("memory2", "store") # async로 실행
    # builder.set_finish_point("store")

    builder.set_finish_point("memory2")

    return builder.compile()
