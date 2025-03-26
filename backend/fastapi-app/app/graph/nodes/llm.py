# app/graph/nodes/llm.py
from langchain_openai import ChatOpenAI

llm = ChatOpenAI(model="gpt-3.5-turbo", temperature=0.8)


async def llm_node(state: dict) -> dict:
    """
    GPT 호출로 ai_response 생성
    """
    chat_prompt = state["chat_prompt"]
    response = await llm.ainvoke(chat_prompt)

    # 후처리: 'AI:' 제거
    content = response.content.strip()
    if content.lower().startswith("ai:"):
        content = content[3:].lstrip()

    state["ai_response"] = content
    return state
