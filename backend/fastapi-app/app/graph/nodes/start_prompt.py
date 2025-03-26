# app/graph/nodes/start_prompt.py
def start_prompt_node(state: dict) -> dict:
    """
    전화 시작 시 topic 기반 system + user prompt 구성
    """
    print("✅ start_prompt_node 실행됨 with topic:", state.get("topic"))

    topic = state.get("topic", "")

    system_prompt = (
        "You are starting a phone call with the user. "
        "Greet them naturally in English based on the given topic.\n"
        "Don't prefix your response with 'AI:' or any speaker label."
    )

    user_prompt = f"Start a phone conversation about this topic: {topic}"

    state["chat_prompt"] = [
        {"role": "system", "content": system_prompt},
        {"role": "user", "content": user_prompt},
    ]

    state["user_input"] = user_prompt  # memory_node용 저장

    return state
