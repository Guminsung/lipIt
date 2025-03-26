# app/graph/nodes/prompt.py
from langchain_core.messages import HumanMessage, AIMessage


def prompt_node(state: dict) -> dict:
    """
    최근 메시지와 RAG 검색 결과를 기반으로 GPT에 줄 역할 분리 프롬프트 구성
    """
    history = state.get("messages", [])[-10:]  # 최근 대화 기록
    context = state.get("retrieved_context", [])
    user_input = state.get("input")

    # 시스템 프롬프트
    system_prompt = (
        "You are on a phone call with the user. "
        "Speak naturally and casually in English.\n"
        "Don't prefix your response with 'AI:' or any speaker label. "
        "Just continue the conversation naturally."
    )
    if context:
        system_prompt += (
            "\n\nRelevant information from previous conversations:\n"
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

    # 역할 분리된 프롬프트 전달
    state["chat_prompt"] = chat_prompt
    state["user_input"] = user_input  # memory_node 저장용
    return state
