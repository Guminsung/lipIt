# app/graph/node/prompt_report.py
from langchain_core.messages import HumanMessage, SystemMessage


def prompt_report_node(state: dict) -> dict:
    messages = state["messages"]

    # 전체 대화 텍스트
    conversation_text = "\n".join(
        [
            f"{'English Tutor' if m.type == 'ai' else '당신'}: {m.content}"
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
You are an assistant generating a structured summary report of a phone conversation between a user and an English tutor. 

📌 Your task is to analyze the conversation and return a polished, natural, and helpful Korean report based on the messages below.

1. 📖 Summary (summary)

- 이번 대화의 **전체 흐름**을 한국어로 간단히 요약해 주세요 (존댓말).
- 무엇에 대해 이야기했는지, 어떤 주제로 흘러갔는지를 서술형으로 정리해주세요.
- 직접 인용보다는 자연스러운 **내러티브 문장**을 사용하세요.
- 너무 단순하게 “~을 말했다”가 아니라, "당신은 처음에 ~을 이야기하다가, 이후 ~로 화제를 옮겼다"처럼 자연스럽게 연결해주세요.
- 최대 100자 내외, 3~4문장.


2. 💡 Feedback (feedback)

- 피드백은 통화 내용에서 사용자가 말한 내용 중 어색한 표현에 대해 수정하는 방식으로 주세요.
- 학생의 영어 사용에 대해 **교사처럼 친절한 말투**로 한국어 피드백을 작성해주세요.
- “당신은 ~” 형태로 직접 말해 주세요.
- 영어 실력, 표현, 문장 구성, 발음, 유창성 등에 대해 구체적으로 코멘트 해 주세요.
- 반드시 **한 가지 이상 구체적인 팁**을 주세요. 예: “'I like' 대신 'I'm into'라고 말하면 더 자연스럽습니다.”
- 영어 단어는 **영문 스펠링**만 사용하세요.
- 100자 내외, 친절하고 긍정적으로 작성하세요.


3. ✍️ **Native Expressions (native_expressions)**

From the user’s original sentences below, extract **up to 3**. For each:

- "my_sentence": original sentence by user.
- "native_sentence": a more natural, native-style English version.
- "keyword": one **key native idiom or phrase** from your version.
- "keyword_kor": basic **dictionary-style** Korean translation of the keyword (e.g., “wrap up” → "마무리하다").


4. 🏷 **Meaningful Messages with Tags (meaningful_messages)**

- Extract up to 5 meaningful user-related utterances from the full conversation.
- These should reflect key interests, preferences, opinions, or personal facts.
- Tags must be specific and meaningful (e.g., "Michael Jackson", "favorite artist").
- Avoid generic words like "sentence", "talk", "English".
- Format each item like this:

{{
  "content": "User: I love Michael Jackson. Tutor: Oh, he's a legend!",
  "tags": ["favorite artist", "Michael Jackson", "music", "hobby"]
}}

- Tags should be short phrases or keywords in English.
- Do not repeat similar messages.
- Output as a list of dicts.

---


--- USER SENTENCES START ---
{user_sentences_block}
--- USER SENTENCES END ---


📢 Notes:

- Only use exact user inputs for "my_sentence"  
- Do NOT paraphrase the English Tutor’s lines  
- Do NOT copy your own previous text  
- Use simple JSON formatting — no markdown, no commentary


Return your answer in **strict JSON format**:

{{
  "summary": "...",
  "feedback": "...",
  "native_expressions": [
    {{
      "my_sentence": "...",
      "native_sentence": "...",
      "keyword": "...",
      "keyword_kor": "..."
    }},
    ...
  ],
  "meaningful_messages": [
    {{
      "content": "...",
      "tags": ["...", "...", "...", "..."]
    }}
  ]
}}
""".strip()

    prompt = [
        SystemMessage(content=system_prompt.strip()),
        HumanMessage(content=conversation_text),
    ]

    state["chat_prompt"] = prompt
    return state
