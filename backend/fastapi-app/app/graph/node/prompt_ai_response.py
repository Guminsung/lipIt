from app.graph.prompt_format.json_prompt_builder import build_json_response_prompt
from app.graph.util.context_formatter import convert_context_to_memory_lines


def prompt_ai_response_node(state: dict) -> dict:
    max_history = 6
    timeout_suffix = " We've been talking for a while. Please end the call politely."

    history = state.get("messages", [])[-max_history:]
    raw_contexts = state.get("retrieved_context", [])
    memory_lines = convert_context_to_memory_lines(raw_contexts)
    user_input = state.get("input", "")
    is_timeout = state.get("is_timeout", False)

    if is_timeout:
        user_input += timeout_suffix

    suffix = """
IMPORTANT: Do not end the call unless the user clearly says goodbye, wants to stop, or mentions ending the conversation.

⚠️ Do NOT end the call for vague or neutral replies like "okay", "drawing", "I like it", etc.
Keep the conversation going unless the user clearly shows intent to end.

Example:
human: I like pizza.
ai: {
  "en": "That's great! Pizza is delicious. Do you have a favorite topping?",
  "ko": "좋아요! 피자는 정말 맛있죠. 좋아하는 토핑이 있나요?",
  "should_end_call": false
}
""".strip()

    # 💬 context를 자연어 기억 문장으로 구성
    context = ""
    if memory_lines:
        context = "Here is relevant memory from past conversations:\n" + "\n".join(
            f"- {line}" for line in memory_lines
        )

    # system prompt 생성
    system_prompt = build_json_response_prompt(context=context, suffix=suffix)

    chat_prompt = [{"role": "system", "content": system_prompt}]
    for msg in history:
        speaker = "human" if msg.type == "human" else "ai"
        chat_prompt.append(
            {"role": "user", "content": f"{speaker}: {msg.content.strip()}"}
        )
    chat_prompt.append({"role": "user", "content": f"human: {user_input.strip()}"})

    state["chat_prompt"] = chat_prompt
    state["user_input"] = user_input

    return state
