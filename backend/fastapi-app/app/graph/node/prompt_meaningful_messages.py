# app/graph/node/prompt_meaningful_messages.py


async def prompt_meaningful_messages_node(state: dict) -> dict:
    history = state.get("messages", [])
    print(f"📢 history = {history}")

    MEANINGLESS_HUMAN_UTTERANCES = {
        "bye",
        "goodbye",
        "ok",
        "okay",
        "thanks",
        "thank you",
        "good",
        "alright",
        "see you",
    }

    # 대화에서 의미 있는 "human + ai" 쌍만 추출
    dialogue_pairs = []
    current_human = None

    def is_meaningful(text: str) -> bool:
        stripped = text.strip().lower()
        return stripped not in MEANINGLESS_HUMAN_UTTERANCES and len(stripped) > 2

    for msg in history:
        if hasattr(msg, "type"):
            role = msg.type
            content = msg.content
        elif msg.__class__.__name__ == "HumanMessage":
            role = "human"
            content = msg.content
        else:
            role = "ai"
            content = msg.content

        if role == "human":
            if is_meaningful(content):
                current_human = content.strip()
            else:
                current_human = None  # 무시
        elif role == "ai" and current_human:
            dialogue_pairs.append((current_human, content.strip()))
            current_human = None

    # 프롬프트
    base_suffix = """
**Meaningful Messages with Tags and Summary Facts (meaningful_messages)**

You will be given a list of meaningful exchanges between human and ai.

- Only include exchanges where the human message is meaningful.
- Extract up to 5 of the most interesting or engaging exchanges.
- Each item must include:
  - "content": a single line in the format "human: ... ai: ..."
  - "tags": 3~5 specific, relevant English keywords
  - "summary_facts": key facts explicitly stated by the user (e.g., name, location, numbers, interests)
- Avoid vague tags like "English", "talk", "sentence"
- Convert digits to words in English only (e.g., "3 books" → "three books") but keep digits in Korean (e.g., "2개")
- Do not include fictional memory — only extract from the provided conversation

Return only valid JSON in the following structure:

{
  "meaningful_messages": [
    {
      "content": "human: ... ai: ...",
      "tags": ["...", "..."],
      "summary_facts": ["..."]
    }
  ]
}
"""

    examples = "\n\n".join([f"human: {h}\nai: {a}" for h, a in dialogue_pairs])
    system_prompt = f"{base_suffix}\n\nConversation:\n{examples}"

    state["chat_prompt"] = [{"role": "system", "content": system_prompt}]
    return state
