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


1. 📖 **Summary (summary)**

- Write a fluent and polite summary in **Korean (존댓말)**
- Include the main topics, user interests, questions, and any important moments  
- Use a smooth, human tone — not mechanical  
- Limit to **100 words**, and write as if explaining to a teacher or coach
- Avoid robotic phrasing. Be slightly narrative and easy to read.


2. 💡 **Feedback (feedback)**

- Provide encouraging but detailed feedback in **Korean (존댓말)**  
- Speak directly to the user — use **“당신의 문장은…”**, **“당신은 잘 하셨어요”**  
- Give suggestions like a supportive teacher
- Comment on communication skills, expressions, grammar, fluency, and listening  
- Suggest at least **one specific tip** (e.g. alternative phrases, pronunciation tip, intonation, sentence structure)
- If you mention any English word in the Korean text (e.g., during feedback), write it in **English spelling**, not in Korean letters
- For example, write “someday”, not “썸데이” or “온데이”
- Stay supportive and warm  
- Limit to **100 words**


3. ✍️ **Native Expressions (native_expressions)**

From the user’s original sentences below, extract **up to 3**. For each:

- "my_sentence": original sentence by user  
- "native_sentence": a more natural, native-style English version  
- "keyword": one **key native idiom or phrase** from your version  
- "keyword_kor": basic **dictionary-style** Korean translation of the keyword (e.g., “wrap up” → "마무리하다")


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
  ]
}}
""".strip()

    prompt = [
        SystemMessage(content=system_prompt.strip()),
        HumanMessage(content=conversation_text),
    ]

    state["chat_prompt"] = prompt
    return state
