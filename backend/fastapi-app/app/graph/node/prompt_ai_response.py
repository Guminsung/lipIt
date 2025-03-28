# app/graph/nodes/prompt.py


# def prompt_node(state: dict) -> dict:
#     """
#     최근 메시지와 RAG 검색 결과를 기반으로 GPT에 줄 역할 분리 프롬프트 구성
#     JSON 형태의 영어 응답 + 한국어 번역을 포함하도록 구성
#     """
#     history = state.get("messages", [])[-10:]  # 최근 대화 기록
#     context = state.get("retrieved_context", [])
#     user_input = state.get("input")
#     is_timeout = state.get("is_timeout", False)

#     if is_timeout:
#         user_input += (
#             "\n\nWe've been talking for a while. Please end the call politely."
#         )

#     # 시스템 프롬프트 구성
#     system_prompt = (
#         "You are an AI speaking on a phone call with a user. "
#         "Respond in a friendly and natural tone in English. "
#         "Your response must be returned in **strict JSON format**, with no explanation or extra text.\n\n"
#         "Respond appropriately based on the situation. If the user seems to want to end the call or the call has gone too long, end it naturally.\n\n"
#         "Return your answer in the following format:\n"
#         "{\n"
#         '  "en": "<Your English reply>",\n'
#         '  "ko": "<Polite and natural Korean translation of the English reply>",\n'
#         '  "should_end_call": true or false\n'
#         "}\n\n"
#         "**Only return the JSON. Do not include any markdown, explanations, or speaker labels like 'AI:'.**"
#     )

#     if context:
#         system_prompt += (
#             "\nHere is relevant information from previous conversations:\n"
#             + "\n".join(context)
#         )

#     # GPT 메시지 포맷 구성
#     chat_prompt = [{"role": "system", "content": system_prompt}]
#     for msg in history:
#         if msg.type == "human":
#             chat_prompt.append({"role": "user", "content": msg.content})
#         elif msg.type == "ai":
#             chat_prompt.append({"role": "assistant", "content": msg.content})

#     # 마지막 user 입력 추가
#     chat_prompt.append({"role": "user", "content": user_input})

#     # state에 저장
#     state["chat_prompt"] = chat_prompt
#     state["user_input"] = user_input

#     return state

# app/graph/nodes/prompt.py
from app.graph.prompt_format.json_prompt_builder import build_json_response_prompt


def prompt_ai_response_node(state: dict) -> dict:
    history = state.get("messages", [])[-10:]
    context = "\n".join(state.get("retrieved_context", []))
    user_input = state.get("input")
    is_timeout = state.get("is_timeout", False)

    if is_timeout:
        user_input += (
            "\n\nWe've been talking for a while. Please end the call politely."
        )

    suffix = "Respond appropriately. If the user seems to want to end the call or the call has gone too long, end it naturally."
    system_prompt = build_json_response_prompt(
        context=context, suffix=suffix, include_should_end=True
    )

    chat_prompt = [{"role": "system", "content": system_prompt}]
    for msg in history:
        role = "user" if msg.type == "human" else "assistant"
        chat_prompt.append({"role": role, "content": msg.content})
    chat_prompt.append({"role": "user", "content": user_input})

    state["chat_prompt"] = chat_prompt
    state["user_input"] = user_input
    return state
