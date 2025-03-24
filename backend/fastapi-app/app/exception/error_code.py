from enum import Enum


class ErrorCode(str, Enum):
    CALL_NOT_FOUND = "CALL-001"
    CALL_ALREADY_ENDED = "CALL-002"
    CALL_INTERNAL_ERROR = "CALL-003"
    CALL_AI_FAILED = "CALL-004"
    CALL_TTS_FAILED = "CALL-005"
