from typing import List


def convert_context_to_memory_lines(contexts: List[str]) -> List[str]:
    """
    사용자 과거 대화 context를 기억하는 듯한 문장으로 바꿔줌.
    예: "사용자는 그림을 자주 그린다고 했다." -> "You mentioned you enjoy drawing."
    """
    memory_lines = []
    for ctx in contexts:
        if "그림" in ctx:
            memory_lines.append("You mentioned you enjoy drawing.")
        elif "사람" in ctx and "그리" in ctx:
            memory_lines.append("You said you like drawing people.")
        elif "감정" in ctx:
            memory_lines.append(
                "You once said capturing emotion is really important to you."
            )
        else:
            memory_lines.append(f"You mentioned: {ctx}")
    return memory_lines
