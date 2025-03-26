# app/graph/nodes/prompt.py
from langchain_core.messages import HumanMessage, AIMessage


def prompt_node(state: dict) -> dict:
    """
    최근 메시지와 RAG 검색 결과를 기반으로 GPT에 줄 역할 분리 프롬프트 구성
    JSON 형태의 영어 응답 + 한국어 번역을 포함하도록 구성
    """
    history = state.get("messages", [])[-10:]  # 최근 대화 기록
    context = state.get("retrieved_context", [])
    user_input = state.get("input")

    # 시스템 프롬프트 구성
    system_prompt = (
        "You are an AI speaking on a phone call with a user. "
        "Respond naturally and casually in English.\n"
        "Return the response in the following JSON format:\n\n"
        "{\n"
        '  "en": "Your English response here",\n'
        '  "ko": "Translate it into polite, natural Korean as if you’re actually talking on the phone. Use 존댓말 (formal and respectful tone)."\n'
        "}\n\n"
        "Do not include any additional text outside the JSON.\n"
        "Respond as if you're continuing a phone call."
    )

    if context:
        system_prompt += (
            "\nHere is relevant information from previous conversations:\n"
            + "\n".join(context)
        )

    # GPT 메시지 포맷 구성
    chat_prompt = [{"role": "system", "content": system_prompt}]
    for msg in history:
        if msg.type == "human":
            chat_prompt.append({"role": "user", "content": msg.content})
        elif msg.type == "ai":
            chat_prompt.append({"role": "assistant", "content": msg.content})

    # 마지막 user 입력 추가
    chat_prompt.append({"role": "user", "content": user_input})

    # state에 저장
    state["chat_prompt"] = chat_prompt
    state["user_input"] = user_input

    return state
