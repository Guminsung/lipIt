from enum import Enum


# class ErrorCode(str, Enum):
#     CALL_NOT_FOUND = "CALL-001"
#     CALL_ALREADY_ENDED = "CALL-002"
#     CALL_INTERNAL_ERROR = "CALL-003"
#     CALL_AI_FAILED = "CALL-004"
#     CALL_TTS_FAILED = "CALL-005"

#     NEWS_QUERY_ERROR = "NEWS-001"
#     NEWS_UPDATE_ERROR = "NEWS-002"
#     NEWS_CRAWL_ERROR = "NEWS-003"

#     AUTH_UNAUTHORIZED = "AUTH-001"
#     AUTH_INVALID_TOKEN = "AUTH-002"
#     AUTH_EXPIRED_TOKEN = "AUTH-003"
#     AUTH_TOKEN_MISSING = "AUTH-004"


class ErrorCode:
    def __init__(self, code: str, message: str):
        self.code = code
        self.message = message

    def __str__(self):
        return f"[{self.code}] {self.message}"


# 실제 에러 코드 정의
class Error:
    # Call
    CALL_NOT_FOUND = ErrorCode("CALL-001", "해당 통화 기록을 찾을 수 없습니다.")
    CALL_ALREADY_ENDED = ErrorCode("CALL-002", "이미 종료된 통화입니다.")
    CALL_INTERNAL_ERROR = ErrorCode("CALL-003", "RAG 검색 중 서버 오류가 발생했습니다.")

    # Auth
    AUTH_UNAUTHORIZED = ErrorCode(
        "AUTH-001", "인증되지 않은 사용자입니다. 로그인 후 다시 시도하세요."
    )
    AUTH_INVALID_TOKEN = ErrorCode("AUTH-002", "유효하지 않은 Access Token입니다.")
    AUTH_EXPIRED_TOKEN = ErrorCode("AUTH-003", "만료된 Access Token입니다.")
    AUTH_TOKEN_MISSING = ErrorCode("AUTH-004", "Access Token이 누락되었습니다.")

    # News
    NEWS_QUERY_ERROR = ErrorCode("NEWS-001", "뉴스 조회 중 서버 오류가 발생했습니다.")
    NEWS_UPDATE_ERROR = ErrorCode(
        "NEWS-002", "뉴스 업데이트 중 서버 오류가 발생했습니다."
    )
    NEWS_CRAWL_ERROR = ErrorCode("NEWS-003", "뉴스 크롤링 중 오류가 발생했습니다.")

    # Audio
    AUDIO_UPLOAD_ERROR = ErrorCode("AUDIO-001", "파일 업로드 중 오류가 발생했습니다.")

    # RAG
    RAG_SEARCH_ERROR = ErrorCode("RAG-001", "RAG 검색 중 서버 오류가 발생했습니다.")
