# app/graph/nodes/llm.py
from app.graph.util.parser import parse_gpt_json_response
from app.graph.llm_client import llm


async def generate_ai_response_node(state: dict) -> dict:
    chat_prompt = state["chat_prompt"]
    response = await llm.ainvoke(chat_prompt)

    result = parse_gpt_json_response(response.content, ["en", "ko", "should_end_call"])
    state["ai_response"] = result["en"] or response.content.strip()
    state["ai_response_kor"] = result["ko"] or ""
    state["should_end_call"] = (
        result["should_end_call"] if result["should_end_call"] is not None else False
    )

    return state
