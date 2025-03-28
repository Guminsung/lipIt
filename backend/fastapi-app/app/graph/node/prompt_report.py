# app/graph/node/report_prompt.py
from langchain_core.messages import HumanMessage, SystemMessage
from app.schema.call import Message


def prompt_report_node(state: dict) -> dict:
    messages = state["messages"]

    # 전체 대화 텍스트
    conversation_text = "\n".join(
        [
            f"{'AI' if m.type == 'ai' else '사용자'}: {m.content}"
            for m in messages
            if hasattr(m, "content") and m.content
        ]
    )

    # 사용자 메시지만 따로 추출
    user_sentences_list = [
        m.content.strip()
        for m in messages
        if m.type == "human" and hasattr(m, "content") and m.content.strip()
    ]

    # 사용자 문장을 문자열로 구성 (프롬프트에 삽입)
    user_sentences_block = "\n".join(f"- {s}" for s in user_sentences_list)

    system_prompt = f"""
You are a helpful assistant that generates a conversation summary, feedback, and native English expressions.

1. 📌 Summarize the overall conversation in **Korean**, using a **natural and polite tone (존댓말)**. The summary should include the **main topics and conclusions**, written in **100 words or fewer**, as if explaining the conversation to a third person. (→ summary)

2. 📌 Provide feedback in **Korean** on the user's communication patterns, including **specific improvement advice and examples**, in **100 words or fewer**. Use a helpful and respectful tone. (→ feedback)

3. 📌 From the following **user-only sentences**, extract up to 3 meaningful ones.
Use these **exactly as they are** for "my_sentence".

📌 Only use sentences from the list below.
❌ Do NOT include any sentences spoken by the AI.

--- USER SENTENCES START ---
{user_sentences_block}
--- USER SENTENCES END ---

4. For each, suggest:
  - A more natural native English expression (→ native_sentence)
  - A key native phrase or idiom from your improved version (→ keyword)
  - A Korean translation of that key phrase (→ keyword_kor)

5. Identify the key phrase or idiom used in the improved sentence (→ keyword), and provide a Korean translation (→ keyword_kor), using the **base dictionary form (기본형)**. For example, if the phrase is “that’s all,” the translation should be “끝내다” or “모두이다” instead of “다 된 것 같아요.”

Return your answer in **strict JSON format**:

{{
  "summary": "한국어 통화 요약 (100단어 이내, 존댓말)",
  "feedback": "한국어 피드백 (100단어 이내, 존댓말)",
  "native_expressions": [
    {{
      "my_sentence": "원문 문장",
      "native_sentence": "자연스러운 표현",
      "keyword": "핵심 표현",
      "keyword_kor": "핵심 표현 한글 번역"
    }},
    ...
  ]
}}

⚠️ Only return the JSON object.
❌ Do NOT include any explanation, markdown, or commentary.
"""

    prompt = [
        SystemMessage(content=system_prompt.strip()),
        HumanMessage(content=conversation_text),
    ]

    state["chat_prompt"] = prompt
    return state
