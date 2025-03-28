package com.arizona.lipit.global.docs.voice;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.arizona.lipit.domain.voice.dto.RecordingVoiceRequestDto;
import com.arizona.lipit.domain.voice.dto.RecordingVoiceResponseDto;
import com.arizona.lipit.domain.voice.dto.SelectVoiceRequestDto;
import com.arizona.lipit.domain.voice.dto.SelectVoiceResponseDto;
import com.arizona.lipit.global.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "음성", description = "음성 관련 API")
public interface VoiceApiSpec {

    @Operation(summary = "음성 선택", description = """
        💡 사용자가 사용할 음성을 선택합니다.
        """)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "음성이 성공적으로 선택되었습니다."),
        @ApiResponse(responseCode = "400", 
            description = """
            `[VOICE-001]` 유효하지 않은 음성 ID입니다.
            `[VOICE-002]` 음성 정보 형식이 올바르지 않습니다.
            """, 
            content = @Content()),
        @ApiResponse(responseCode = "401", 
            description = """
            `[AUTH-001]` 인증되지 않은 사용자입니다. 로그인 후 다시 시도하세요.
            `[AUTH-002]` 유효하지 않은 토큰입니다.
            """, 
            content = @Content()),
        @ApiResponse(responseCode = "404", 
            description = """
            `[VOICE-003]` 요청한 음성을 찾을 수 없습니다.
            `[MEMBER-001]` 해당 ID를 가진 사용자를 찾을 수 없습니다.
            """, 
            content = @Content()),
        @ApiResponse(responseCode = "500", 
            description = "`[VOICE-011]` 음성 처리 중 서버 오류가 발생했습니다.", 
            content = @Content())
    })
    ResponseEntity<CommonResponse<SelectVoiceResponseDto>> selectVoice(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable @Parameter(description = "회원 ID") Long memberId,
        @Valid @RequestBody SelectVoiceRequestDto requestDto
    );

    @Operation(summary = "커스텀 음성 저장", description = """
        💡 사용자의 커스텀 음성을 저장합니다.
        """)
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "음성이 성공적으로 저장되었습니다."),
        @ApiResponse(responseCode = "400", 
            description = """
            `[VOICE-002]` 음성 정보 형식이 올바르지 않습니다.
            `[VOICE-005]` 유효하지 않은 URL 형식입니다.
            """, 
            content = @Content()),
        @ApiResponse(responseCode = "401", 
            description = """
            `[AUTH-001]` 인증되지 않은 사용자입니다. 로그인 후 다시 시도하세요.
            `[AUTH-002]` 유효하지 않은 토큰입니다.
            """, 
            content = @Content()),
        @ApiResponse(responseCode = "404", 
            description = "`[MEMBER-001]` 해당 ID를 가진 사용자를 찾을 수 없습니다.", 
            content = @Content()),
        @ApiResponse(responseCode = "409", 
            description = "`[VOICE-010]` 이미 존재하는 음성 이름입니다.", 
            content = @Content()),
        @ApiResponse(responseCode = "500", 
            description = "`[VOICE-011]` 음성 처리 중 서버 오류가 발생했습니다.", 
            content = @Content())
    })
    ResponseEntity<CommonResponse<RecordingVoiceResponseDto>> saveRecordingVoice(
        @AuthenticationPrincipal UserDetails userDetails,
        @Valid @RequestBody RecordingVoiceRequestDto requestDto
    );
}