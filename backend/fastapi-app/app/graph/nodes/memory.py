# 📁 app/graph/nodes/memory.py

from langchain_core.messages import HumanMessage, AIMessage, BaseMessage
from datetime import datetime
from app.schema.call import Message
from app.util.datetime_utils import iso_now_kst


def convert_lc_message(m: BaseMessage) -> Message:
    return Message(
        type=m.type,
        content=m.content,
        content_kor=None,
        audio_url=None,
        timestamp=iso_now_kst(),
    )


def safe_convert_message_to_dict(msg):
    if isinstance(msg, BaseMessage):
        return convert_lc_message(msg).dict()
    elif isinstance(msg, Message):
        return msg.dict()
    elif isinstance(msg, dict):
        return msg
    else:
        raise ValueError("Message must be a Message, dict, or BaseMessage type")


def convert_to_lc_message(m: Message) -> BaseMessage:
    if m.type == "human":
        return HumanMessage(content=m.content)
    elif m.type == "ai":
        return AIMessage(content=m.content)
    else:
        raise ValueError(f"Unknown type: {m.type}")


def memory_node(state: dict) -> dict:
    """
    input 필드를 messages에 추가 (user 또는 ai)
    LangChain 메시지를 Message로 변환
    """
    input_text = state.get("input")
    ai_response = state.get("ai_response")

    state.setdefault("messages", [])

    if ai_response:
        state["messages"].append(convert_lc_message(AIMessage(content=ai_response)))
    elif input_text:
        state["messages"].append(convert_lc_message(HumanMessage(content=input_text)))

    return state
