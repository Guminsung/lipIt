# app/graph/nodes/llm.py
import json
from langchain_openai import ChatOpenAI

llm = ChatOpenAI(model="gpt-3.5-turbo", temperature=0.8)


async def llm_node(state: dict) -> dict:
    """
    GPT 호출로 ai_response 생성
    """
    chat_prompt = state["chat_prompt"]
    response = await llm.ainvoke(chat_prompt)

    try:
        response_json = json.loads(response.content.strip())
        state["ai_response"] = response_json.get("en")
        state["ai_response_kor"] = response_json.get("ko", "")
        state["should_end_call"] = response_json.get("should_end_call", False)
    except json.JSONDecodeError:
        # 예외 처리: JSON 파싱 실패 시 전체 content 저장
        # state["ai_response"] = response.content.strip()
        print("🚫 JSON Decode Error")
        print("💬 GPT Raw Response:", response.content)
        state["ai_response"] = response.content.strip()
        state["ai_response_kor"] = ""
        state["should_end_call"] = False  # fallback

    return state
