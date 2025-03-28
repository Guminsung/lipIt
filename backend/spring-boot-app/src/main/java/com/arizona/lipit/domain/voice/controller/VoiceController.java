package com.arizona.lipit.domain.voice.controller;

import com.arizona.lipit.domain.voice.dto.SelectVoiceRequestDto;
import com.arizona.lipit.domain.voice.dto.SelectVoiceResponseDto;
import com.arizona.lipit.domain.voice.dto.UserVoiceResponseDto;
import com.arizona.lipit.domain.voice.dto.VoiceResponseDto;
import com.arizona.lipit.domain.voice.dto.RecordingVoiceRequestDto;
import com.arizona.lipit.domain.voice.dto.RecordingVoiceResponseDto;
import com.arizona.lipit.domain.voice.dto.CelebVoiceResponseDto;
import com.arizona.lipit.domain.voice.service.VoiceService;
import com.arizona.lipit.global.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/voices")
@RequiredArgsConstructor
public class VoiceController {

    private final VoiceService voiceService;

    @GetMapping("/celeb")
    public ResponseEntity<CommonResponse<List<CelebVoiceResponseDto>>> getCelebVoices(@RequestParam Long memberId) {
        List<CelebVoiceResponseDto> voices = voiceService.getCelebVoicesByMemberId(memberId);
        return ResponseEntity.ok(CommonResponse.ok("연예인 음성이 성공적으로 조회되었습니다.", voices));
    }
    
    @GetMapping("/custom")
    public ResponseEntity<CommonResponse<List<VoiceResponseDto>>> getCustomVoices(@RequestParam Long memberId) {
        List<VoiceResponseDto> voices = voiceService.getCustomVoicesByMemberId(memberId);
        return ResponseEntity.ok(CommonResponse.ok("커스텀 음성이 성공적으로 조회되었습니다.", voices));
    }

    @GetMapping("users/{memberId}/voice")
    public ResponseEntity<CommonResponse<List<UserVoiceResponseDto>>> getUserVoices(@PathVariable Long memberId) {
        List<UserVoiceResponseDto> voices = voiceService.getAllVoicesByMemberId(memberId);
        return ResponseEntity.ok(CommonResponse.ok("사용자 음성이 성공적으로 조회되었습니다.", voices));
    }
    
    @PatchMapping("users/{memberId}/voice")
    public ResponseEntity<CommonResponse<SelectVoiceResponseDto>> selectVoice(
            @PathVariable Long memberId,
            @RequestBody SelectVoiceRequestDto requestDto) {
        SelectVoiceResponseDto responseDto = voiceService.selectVoice(memberId, requestDto);
        return ResponseEntity.ok(CommonResponse.ok("음성이 성공적으로 선택되었습니다.", responseDto));
    }

    @PostMapping("/voices/recording")
    public ResponseEntity<CommonResponse<RecordingVoiceResponseDto>> saveRecordingVoice(
            @RequestBody RecordingVoiceRequestDto requestDto,
            @RequestParam Long memberId) {
        RecordingVoiceResponseDto responseDto = voiceService.saveRecordingVoice(requestDto, memberId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.ok("요청이 성공적으로 처리되었습니다.", responseDto));
    }
}