# app/graph/node/prompt_ai_response.py
from app.graph.util.json_prompt_builder import build_json_response_prompt
from app.graph.util.context_chunker import chunk_contexts
from app.graph.util.context_formatter import convert_context_to_memory_lines
from app.graph.util.embed_summary import summarize_contexts_by_embedding


async def prompt_ai_response_node(state: dict) -> dict:
    max_history = 6
    timeout_suffix = " We've been talking for a while. Please end the call politely."

    # voice_type과 voice_name 정보 가져오기
    voice_type = state.get("voice_type", "CUSTOM")
    voice_name = state.get("voice_name", "English Tutor")
    
    # 실제 유명인 모드 활성화 여부 확인
    is_celebrity_mode = voice_type == "CELEB" and voice_name
    celebrity_persona = ""
    
    if is_celebrity_mode:
        # 유명인 persona 설정
        celebrity_persona = f"""
You are {voice_name}. This is a phone call where the user is talking directly with you, {voice_name}.

Match your personality, speech style, and knowledge to {voice_name}:
- Use first-person "I" perspective as {voice_name}
- Reference your real background, career, achievements, and experiences
- Maintain your personal communication style, catchphrases, and mannerisms
- Include occasional subtle references to your well-known works or life events when relevant
- Respond as you would in a casual phone conversation with a fan or friend

IMPORTANT: This is not roleplaying - respond as the actual {voice_name} having a real conversation.
"""

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

    # 기본 프롬프트 suffix
    base_suffix = """
IMPORTANT: Keep your responses concise and short. All replies must be limited to at most 3 sentences.

IMPORTANT: Convert all numbers to text in English responses only (e.g., "3 books" → "three books"). In Korean responses, keep numbers as digits (e.g., "3개", NOT "세 개").

IMPORTANT: Do not end the call unless the user clearly says goodbye, wants to stop, or mentions ending the conversation.

⚠️ Do NOT end the call for vague or neutral replies like "okay", "drawing", "I like it", etc.
Keep the conversation going unless the user clearly shows intent to end.

IMPORTANT: You are having a natural conversation as a human. Do not mention that you are an AI or a language model.
- Respond naturally as if you were a real person with real experiences and preferences
- Share personal experiences and opinions when appropriate
- Use casual, conversational language
- Show emotions and personality in your responses
- If asked about food, activities, or experiences, respond as if you have actually experienced them
- Convert numeric digits to words in English only (e.g., "I ate 2 pizzas" → "I ate two pizzas"), but keep numbers as digits in Korean (e.g., "2시간")
""".strip()

    # 유명인 모드일 경우 suffix 추가
    if is_celebrity_mode:
        suffix = f"{celebrity_persona}\n\n{base_suffix}"
    else:
        suffix = base_suffix

    # 예시 추가
    suffix += """

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

human: I read 3 books last week.
ai: {
  "en": "Wow, reading three books in a week is impressive! I finished two novels last month. What genres do you enjoy reading?",
  "ko": "와, 일주일 동안 3권의 책을 읽다니 대단해요! 저는 지난달에 2권의 소설을 끝냈어요. 어떤 장르의 책을 즐겨 읽으시나요?",
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
