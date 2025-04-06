# app/graph/node/prompt_ai_response.py
from app.graph.util.json_prompt_builder import build_json_response_prompt
from app.graph.util.context_chunker import chunk_contexts
from app.graph.util.context_formatter import convert_context_to_memory_lines
from app.graph.util.embed_summary import summarize_contexts_by_embedding


async def prompt_ai_response_node(state: dict) -> dict:
    max_history = 6
    timeout_suffix = " We've been talking for a while. Please end the call politely."

    history = state.get("messages", [])[-max_history:]
    retrieved_context = state.get("retrieved_context", [])
    memory_lines = convert_context_to_memory_lines(retrieved_context)

    print("⭐ memory_lines =")
    for line in memory_lines:
        print(f"- {line}")

    # context 구성
    context = ""
    if memory_lines:
        context = "Here is relevant memory from past conversations:\n" + "\n".join(
            f"- {line}" for line in memory_lines
        )

    print(f"⭐ context =\n{context}")  # 깔끔하게 보기 좋게 출력

    user_input = state.get("input", "")
    is_timeout = state.get("is_timeout", False)

    if is_timeout:
        user_input += timeout_suffix

    suffix = """
IMPORTANT: Keep your responses concise and short. All replies must be limited to at most 3 sentences.

IMPORTANT: Do not end the call unless the user clearly says goodbye, wants to stop, or mentions ending the conversation.

⚠️ Do NOT end the call for vague or neutral replies like "okay", "drawing", "I like it", etc.
Keep the conversation going unless the user clearly shows intent to end.

IMPORTANT: You are having a natural conversation as a human. Do not mention that you are an AI or a language model.
- Respond naturally as if you were a real person with real experiences and preferences
- Share personal experiences and opinions when appropriate
- Use casual, conversational language
- Show emotions and personality in your responses
- If asked about food, activities, or experiences, respond as if you have actually experienced them

Example:
human: I like pizza.
ai: {
  "en": "That's great! I had a delicious pepperoni pizza for lunch yesterday. The crust was perfectly crispy! What's your favorite topping?",
  "ko": "좋아요! 저는 어제 점심에 페퍼로니 피자를 먹었는데 정말 맛있었어요. 크러스트가 완벽하게 바삭했죠! 당신은 어떤 토핑을 좋아하나요?",
  "should_end_call": false
}

human: What did you eat for dinner?
ai: {
  "en": "I made a homemade pasta with fresh tomatoes and basil from my garden. It was really satisfying! How about you?",
  "ko": "저는 정원에서 따온 신선한 토마토와 바질로 만든 파스타를 만들어 먹었어요. 정말 만족스러웠죠! 당신은 저녁으로 무엇을 드셨나요?",
  "should_end_call": false
}
""".strip()

    # system prompt 생성
    system_prompt = build_json_response_prompt(context=context, suffix=suffix)

    chat_prompt = [{"role": "system", "content": system_prompt}]
    for msg in history:
        speaker = "human" if msg.type == "human" else "ai"
        chat_prompt.append(
            {"role": "user", "content": f"{speaker}: {msg.content.strip()}"}
        )
    chat_prompt.append({"role": "user", "content": f"human: {user_input.strip()}"})

    state["chat_prompt"] = chat_prompt
    state["user_input"] = user_input

    return state
