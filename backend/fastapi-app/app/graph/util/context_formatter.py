# app/graph/util/convert_context.py
from typing import List, Dict


def convert_context_to_memory_lines(contexts: List[Dict]) -> List[str]:
    """
    RAG로 검색된 context를 '기억하는 듯한 문장'으로 변환
    각 context는 "human: ... ai: ..." 형식의 메시지임
    """

    memory_lines = []

    for ctx in contexts:
        content = ctx.get("content", "")
        if not content:
            continue

        # 예시 변환: human: I love music. ai: Oh, what kind? -> You once said you love music.
        if content.startswith("human:"):
            first_line = content.split("ai:")[0].strip()
            user_text = first_line.replace("human:", "").strip()
            if user_text:
                memory_lines.append(f'You once said: "{user_text}"')

    return memory_lines
