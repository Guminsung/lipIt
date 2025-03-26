# app/graph/state.py
class CallState(dict):
    """LangGraph 상태 (입출력 공통)"""

    call_id: int
    member_id: int
    input: str  # 사용자 입력
    topic: str | None  # 시작 시 주제
    messages: list  # 전체 메시지 기록
    ai_response: str
    ai_audio_url: str
