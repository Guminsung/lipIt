# app/graph/call_graph.py

from langgraph.graph import StateGraph
from app.graph.node import (
    memory_node,
    prompt_start_call_node,
    prompt_end_call_node,
    rag_node,
    prompt_ai_response_node,
    llm_node,
    parse_ai_response_node,
    tts_node,
)

from app.graph.schema.state import CallState


def build_start_call_graph():
    builder = StateGraph(CallState)
    builder.add_node("start_prompt", prompt_start_call_node)
    builder.add_node("llm", llm_node)
    builder.add_node("parse", parse_ai_response_node)
    builder.add_node("tts", tts_node)
    builder.add_node("memory", memory_node)

    builder.set_entry_point("start_prompt")
    builder.add_edge("start_prompt", "llm")
    builder.add_edge("llm", "parse")
    builder.add_edge("parse", "tts")
    builder.add_edge("tts", "memory")
    builder.set_finish_point("memory")
    return builder.compile()


def build_add_message_graph():
    builder = StateGraph(CallState)
    builder.add_node("memory", memory_node)
    builder.add_node("rag", rag_node)
    builder.add_node("prompt", prompt_ai_response_node)
    builder.add_node("llm", llm_node)
    builder.add_node("parse", parse_ai_response_node)
    builder.add_node("tts", tts_node)
    builder.add_node("memory2", memory_node)

    builder.set_entry_point("memory")
    builder.add_edge("memory", "rag")
    builder.add_edge("rag", "prompt")
    builder.add_edge("prompt", "llm")
    builder.add_edge("llm", "parse")
    builder.add_edge("parse", "tts")
    builder.add_edge("tts", "memory2")
    builder.set_finish_point("memory2")
    return builder.compile()


def build_end_call_graph():
    builder = StateGraph(CallState)
    builder.add_node("memory", memory_node)
    builder.add_node("end_prompt", prompt_end_call_node)
    builder.add_node("llm", llm_node)
    builder.add_node("parse", parse_ai_response_node)
    builder.add_node("tts", tts_node)
    builder.add_node("memory2", memory_node)

    builder.set_entry_point("memory")
    builder.add_edge("memory", "end_prompt")
    builder.add_edge("end_prompt", "llm")
    builder.add_edge("llm", "parse")
    builder.add_edge("parse", "tts")
    builder.add_edge("tts", "memory2")
    builder.set_finish_point("memory2")
    return builder.compile()
