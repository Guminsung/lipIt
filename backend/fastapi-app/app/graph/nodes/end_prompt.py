# app/graph/nodes/end_prompt.py


def end_prompt_node(state: dict) -> dict:
    """
    통화 종료 멘트를 위한 역할 분리 프롬프트 구성
    """
    system_prompt = (
        "You are ending a phone call with the user. "
        "Wrap up the conversation in a friendly, casual tone in English.\n"
        "Don't start your response with 'AI:' or any speaker label."
    )

    user_prompt = "End the phone conversation in a friendly and natural way."

    state["chat_prompt"] = [
        {"role": "system", "content": system_prompt},
        {"role": "user", "content": user_prompt},
    ]

    state["user_input"] = user_prompt  # memory_node 저장용
    return state
