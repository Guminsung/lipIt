# app/api/v1/call_routes.py
from app.api.v1.endpoint.call import (
    create_call_endpoint,
    add_message_to_call_endpoint,
    end_call_endpoint,
)
from app.core.base_router import BaseRouter
from app.schema.call import AIMessageResponse, EndCallResponse, StartCallResponse
from app.schema.common import APIResponse
from app.exception.error_code import ErrorCode

router = BaseRouter(prefix="/api/calls", tags=["Calls"])

router.api_doc(
    path="/",
    endpoint=create_call_endpoint,
    methods=["POST"],
    response_model=APIResponse[StartCallResponse],
    success_model=StartCallResponse,
    success_example={
        "callId": 1,
        "startTime": "2025-03-14T14:00:00.000Z",
        "aiFirstMessage": "Hi! Do you like sports?",
    },
    errors={
        500: {
            "message": "통화 시작 중 서버 오류가 발생했습니다.",
            "code": ErrorCode.CALL_INTERNAL_ERROR,
        }
    },
    summary="📞 통화 시작",
    description="AI 음성 ID와 대화 주제를 기반으로 새로운 통화를 생성하고 첫 메시지를 반환합니다.",
)

router.api_doc(
    path="/{callId}/messages",
    endpoint=add_message_to_call_endpoint,
    methods=["PATCH"],
    response_model=APIResponse[AIMessageResponse],
    success_model=AIMessageResponse,
    success_example={
        "aiMessage": "That sounds interesting!",
        "aiAudioUrl": "https://s3.amazonaws.com/ai_audio/response_1234567890.mp3",
    },
    errors={
        404: {
            "message": "해당 통화 기록을 찾을 수 없습니다.",
            "code": ErrorCode.CALL_NOT_FOUND,
        },
        500: {
            "message": "메시지 처리 중 서버 오류가 발생했습니다.",
            "code": ErrorCode.CALL_INTERNAL_ERROR,
        },
    },
    summary="📞 메시지 추가",
    description="사용자의 메시지를 저장하고, AI의 응답 메시지를 messages 리스트에 추가합니다.",
)

router.api_doc(
    path="/{callId}/end",
    endpoint=end_call_endpoint,
    methods=["PATCH"],
    response_model=APIResponse[EndCallResponse],
    success_model=EndCallResponse,
    success_example={
        "callId": 1,
        "endTime": "2025-03-14T14:25:45.678Z",
        "duration": 315,
        "aiEndMessage": "It was great talking with you today! Have a wonderful day!",
    },
    errors={
        400: {
            "message": "이미 종료된 통화입니다.",
            "code": ErrorCode.CALL_ALREADY_ENDED,
        },
        404: {
            "message": "해당 통화 기록을 찾을 수 없습니다.",
            "code": ErrorCode.CALL_NOT_FOUND,
        },
        500: {
            "message": "통화 종료 중 서버 오류가 발생했습니다.",
            "code": ErrorCode.CALL_INTERNAL_ERROR,
        },
    },
    summary="📞 통화 종료",
    description="사용자 요청 또는 시간 초과 등으로 통화를 종료하고 종료 메시지를 기록합니다.",
)
