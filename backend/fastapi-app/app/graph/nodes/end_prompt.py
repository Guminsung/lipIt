# app/graph/nodes/end_prompt.py


def end_prompt_node(state: dict) -> dict:
    """
    통화 종료 멘트를 위한 역할 분리 프롬프트 구성
    응답은 영어 + 한국어 번역을 포함한 JSON 형식으로 반환
    """
    system_prompt = (
        "You are ending a phone call with the user. "
        "Wrap up the conversation in a friendly, casual tone in English.\n\n"
        "Return your response in the following JSON format:\n"
        "{\n"
        '  "en": "Your English response here",\n'
        '  "ko": "Translate it into polite, natural Korean as if you’re actually talking on the phone. Use 존댓말 (formal and respectful tone)."\n'
        "}\n\n"
        "Do not include any additional text outside the JSON.\n"
    )

    user_prompt = "End the phone conversation in a friendly and natural way."

    state["chat_prompt"] = [
        {"role": "system", "content": system_prompt},
        {"role": "user", "content": user_prompt},
    ]

    state["user_input"] = user_prompt  # memory_node 저장용

    return state
