from app.api.v1.endpoint.call import (
    create_call_endpoint,
    add_message_to_call_endpoint,
    end_call_endpoint,
)
from app.core.base_router import BaseRouter
from app.schema.call import (
    AIMessageResponse,
    EndCallRequest,
    EndCallResponse,
    StartCallRequest,
    StartCallResponse,
    UserMessageRequest,
)
from app.schema.common import APIResponse
from app.exception.error_code import ErrorCode

router = BaseRouter(prefix="/api/calls", tags=["Call"])

router.api_doc(
    path="",
    endpoint=create_call_endpoint,
    methods=["POST"],
    request_model=StartCallRequest,
    response_model=APIResponse[StartCallResponse],
    request_example={
        "callRequestId": 1,
        "memberId": 1,
        "voiceId": 1,
        "voiceAudioUrl": "https://s3_address.com/",
        "topic": "SPORTS",
    },
    success_model=StartCallResponse,
    success_example={
        "callId": 1,
        "startTime": "2025-03-14T14:20:30.123Z",
        "aiMessage": "Hello! How are you doing? I heard you're a big sports enthusiast. What's your favorite sport to watch or play?",
        "aiMessageKor": "안녕하세요! 어떻게 지내고 계신가요? 스포츠 애호가라고 들었는데요. 가장 좋아하는 스포츠는 무엇인가요?",
        "aiAudioUrl": "https://dlxayir1dj7sa.cloudfront.net/audio/ff76e36c-b488-4f7c-9264-6c9b5670dba2.mp3",
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
    request_model=UserMessageRequest,
    response_model=APIResponse[AIMessageResponse],
    success_model=AIMessageResponse,
    request_example={
        "userMessage": "I don't like sports that much.",
        "userMessageKor": "저는 스포츠를 그다지 좋아하지 않습니다.",
    },
    success_example={
        "aiMessage": "That's totally fine! Sports aren't for everyone. So, besides listening to music, what else do you enjoy doing in your free time?",
        "aiMessageKor": "괜찮습니다! 스포츠가 모든 사람을 위한 것은 아닙니다. 그렇다면 음악을 듣는 것 외에도 여가 시간에 즐기는 것은 무엇인가요?",
        "aiAudioUrl": "https://dlxayir1dj7sa.cloudfront.net/audio/bc789f57-732a-4d66-9256-55a5aa3cbfb4.mp3",
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
    request_model=EndCallRequest,
    response_model=APIResponse[EndCallResponse],
    success_model=EndCallResponse,
    request_example={
        "userMessage": "I think that’s all for today.",
        "userMessageKor": "오늘은 여기까지인 것 같습니다.",
        "endReason": "USER_REQUEST",
    },
    success_example={
        "endTime": "2025-03-14T14:25:45.678Z",
        "duration": 315,
        "aiMessage": "Alright, sounds good! If you have any more questions in the future, feel free to reach out. Have a great day!",
        "aiMessageKor": "좋아요, 좋아요! 앞으로 더 궁금한 점이 있으면 언제든지 질문해 주세요. 좋은 하루 되세요!",
        "aiAudioUrl": "https://dlxayir1dj7sa.cloudfront.net/audio/65991ce5-5332-435a-8c23-4be6c92749a9.mp3",
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
