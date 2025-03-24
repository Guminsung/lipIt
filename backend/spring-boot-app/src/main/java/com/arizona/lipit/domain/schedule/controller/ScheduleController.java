package com.arizona.lipit.domain.schedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.arizona.lipit.domain.auth.entity.Member;
import com.arizona.lipit.domain.auth.repository.MemberRepository;
import com.arizona.lipit.domain.schedule.dto.ScheduleDeleteResponseDto;
import com.arizona.lipit.domain.schedule.dto.ScheduleRequestDto;
import com.arizona.lipit.domain.schedule.dto.ScheduleResponseDto;
import com.arizona.lipit.domain.schedule.service.ScheduleService;
import com.arizona.lipit.global.docs.schedule.ScheduleApiSpec;
import com.arizona.lipit.global.exception.CustomException;
import com.arizona.lipit.global.exception.ErrorCode;
import com.arizona.lipit.global.response.CommonResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class ScheduleController implements ScheduleApiSpec {

    private final ScheduleService scheduleService;
    private final MemberRepository memberRepository;

    @GetMapping
    public ResponseEntity<CommonResponse<List<ScheduleResponseDto>>> getAllSchedules(
            @AuthenticationPrincipal UserDetails userDetails) {
        // 이메일로 사용자 ID 조회
        String email = userDetails.getUsername();
        Long memberId = memberRepository.findByEmail(email)
                .map(Member::getMemberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<ScheduleResponseDto> schedules = scheduleService.getAllSchedulesByMemberId(memberId);
        return ResponseEntity.ok(CommonResponse.ok("일정이 성공적으로 조회되었습니다.", schedules));
    }

    @PostMapping
    public ResponseEntity<CommonResponse<ScheduleResponseDto>> createSchedule(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ScheduleRequestDto requestDto) {
        // 이메일로 사용자 ID 조회
        String email = userDetails.getUsername();
        Long memberId = memberRepository.findByEmail(email)
                .map(Member::getMemberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        ScheduleResponseDto createdSchedule = scheduleService.createSchedule(memberId, requestDto);
        return ResponseEntity.ok(CommonResponse.created("일정이 성공적으로 저장되었습니다.", createdSchedule));
    }

    @PatchMapping("/{callScheduleId}")
    public ResponseEntity<CommonResponse<ScheduleResponseDto>> updateSchedule(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long callScheduleId,
            @Valid @RequestBody ScheduleRequestDto requestDto) {
        // 이메일로 사용자 ID 조회
        String email = userDetails.getUsername();
        Long memberId = memberRepository.findByEmail(email)
                .map(Member::getMemberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        ScheduleResponseDto updatedSchedule = scheduleService.updateSchedule(callScheduleId, memberId, requestDto);
        return ResponseEntity.ok(CommonResponse.ok("일정이 성공적으로 수정되었습니다.", updatedSchedule));
    }

    @DeleteMapping("/{callScheduleId}")
    public ResponseEntity<CommonResponse<ScheduleDeleteResponseDto>> deleteSchedule(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long callScheduleId) {
        // 이메일로 사용자 ID 조회
        String email = userDetails.getUsername();
        Long memberId = memberRepository.findByEmail(email)
                .map(Member::getMemberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        ScheduleDeleteResponseDto response = scheduleService.deleteSchedule(callScheduleId, memberId);
        return ResponseEntity.ok(CommonResponse.ok("일정이 성공적으로 삭제되었습니다.", response));
    }
}