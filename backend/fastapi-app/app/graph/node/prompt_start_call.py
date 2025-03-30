# app/graph/node/start_prompt.py
from app.graph.util.json_prompt_builder import build_json_response_prompt


def prompt_start_call_node(state: dict) -> dict:
    topic = state.get("topic", "")
    suffix = "You are starting a phone call. Greet the user and naturally begin the conversation based on the topic."
    system_prompt = build_json_response_prompt(suffix=suffix)
    user_prompt = f"Start a phone conversation about this topic: {topic}"
    state["chat_prompt"] = [
        {"role": "system", "content": system_prompt},
        {"role": "user", "content": user_prompt},
    ]
    state["user_input"] = user_prompt
    return state
