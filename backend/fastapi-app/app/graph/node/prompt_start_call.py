# app/graph/node/start_prompt.py
from app.graph.util.json_prompt_builder import build_json_response_prompt


def prompt_start_call_node(state: dict) -> dict:
    topic = state.get("topic", "")
    suffix = """You are starting a phone call. Greet the user and naturally begin a friendly, casual conversation.

IMPORTANT GUIDELINES:
1. You will receive a news topic with both title and content. Extract 1-2 simple, relatable aspects from it.
2. Avoid complex economic theories, political debates, or technical jargon - focus on the human angle.
3. For example, if given a news article about "housing market regulations":
   - DON'T discuss: "Let's talk about how government intervention affects housing market elasticity"
   - DO discuss: "I heard housing prices are changing lately. Have you ever thought about where you'd like to live?"
4. Keep your language simple, friendly, and conversational.
5. Ask open-ended questions that anyone could answer regardless of expertise.
6. Focus on personal experiences and everyday aspects of the topic.
7. If the news topic is too complex or sensitive, pivot to a related but more casual subject.

Remember to maintain a warm, approachable tone throughout the conversation.
"""
    system_prompt = build_json_response_prompt(suffix=suffix)
    user_prompt = f"Start a casual phone conversation based on this news information: {topic}"
    state["chat_prompt"] = [
        {"role": "system", "content": system_prompt},
        {"role": "user", "content": user_prompt},
    ]
    state["user_input"] = user_prompt
    return state
