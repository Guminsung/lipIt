# app/graph/nodes/start_prompt.py
# def start_prompt_node(state: dict) -> dict:
#     """
#     전화 시작 시 topic 기반 system + user prompt 구성
#     """
#     topic = state.get("topic", "")

#     system_prompt = (
#         "You are starting a phone call with the user.\n"
#         "Respond naturally in English based on the topic.\n"
#         "Then translate your message into Korean.\n"
#         "Return your response strictly in the following JSON format:\n\n"
#         "{\n"
#         '  "en": "Your natural English response",\n'
#         '  "ko": "Translate it into polite, natural Korean as if you’re actually talking on the phone. Use 존댓말 (formal and respectful tone)."\n'
#         "}\n\n"
#         "Do not include any additional text outside the JSON."
#     )

#     user_prompt = f"Start a phone conversation about this topic: {topic}"

#     state["chat_prompt"] = [
#         {"role": "system", "content": system_prompt},
#         {"role": "user", "content": user_prompt},
#     ]

#     state["user_input"] = user_prompt  # memory_node용 저장

#     return state


# app/graph/nodes/start_prompt.py
from app.graph.prompt_format.json_prompt_builder import build_json_response_prompt


def prompt_start_call_node(state: dict) -> dict:
    topic = state.get("topic", "")
    suffix = "You are starting a phone call. Greet the user and naturally begin the conversation based on the topic."
    system_prompt = build_json_response_prompt(suffix=suffix)
    user_prompt = f"Start a phone conversation about this topic: {topic}"
    state["chat_prompt"] = [
        {"role": "system", "content": system_prompt},
        {"role": "user", "content": user_prompt},
    ]
    state["user_input"] = user_prompt
    return state
