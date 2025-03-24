def get_error_code_reference() -> str:
    return """
### 📌 Error Code Reference

| ErrorCode | Status | 설명 |
|-----------|--------|------|
| `AUTH-001` | 401 | 인증되지 않은 사용자입니다. |
| `AUTH-002` | 401 | 유효하지 않은 토큰입니다. |
| `CALL-001` | 404 | 해당 통화 기록을 찾을 수 없습니다. |
| `CALL-002` | 400 | 이미 종료된 통화입니다. |
| `CALL-003` | 500 | 서버 내부 오류 |
"""
