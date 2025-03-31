package com.arizona.lipit.domain.schedule.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.arizona.lipit.domain.schedule.dto.ScheduleDeleteResponseDto;
import com.arizona.lipit.domain.schedule.dto.ScheduleRequestDto;
import com.arizona.lipit.domain.schedule.dto.ScheduleResponseDto;
import com.arizona.lipit.domain.schedule.service.ScheduleService;
import com.arizona.lipit.global.docs.schedule.ScheduleApiSpec;
import com.arizona.lipit.global.response.CommonResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class ScheduleController implements ScheduleApiSpec {

	private final ScheduleService scheduleService;

	@GetMapping
	public ResponseEntity<CommonResponse<List<ScheduleResponseDto>>> getAllSchedules(
		@RequestParam Long memberId) {
		List<ScheduleResponseDto> schedules = scheduleService.getAllSchedulesByMemberId(memberId);
		return ResponseEntity.ok(CommonResponse.ok("일정이 성공적으로 조회되었습니다.", schedules));
	}

	@PostMapping
	public ResponseEntity<CommonResponse<ScheduleResponseDto>> createSchedule(
		@RequestParam Long memberId,
		@Valid @RequestBody ScheduleRequestDto requestDto) {
		ScheduleResponseDto createdSchedule = scheduleService.createSchedule(memberId, requestDto);
		return ResponseEntity.ok(CommonResponse.created("일정이 성공적으로 저장되었습니다.", createdSchedule));
	}

	@PatchMapping("/{callScheduleId}")
	public ResponseEntity<CommonResponse<ScheduleResponseDto>> updateSchedule(
		@RequestParam Long memberId,
		@PathVariable Long callScheduleId,
		@Valid @RequestBody ScheduleRequestDto requestDto) {
		ScheduleResponseDto updatedSchedule = scheduleService.updateSchedule(callScheduleId, memberId, requestDto);
		return ResponseEntity.ok(CommonResponse.ok("일정이 성공적으로 수정되었습니다.", updatedSchedule));
	}

	@DeleteMapping("/{callScheduleId}")
	public ResponseEntity<CommonResponse<ScheduleDeleteResponseDto>> deleteSchedule(
		@RequestParam Long memberId,
		@PathVariable Long callScheduleId) {
		ScheduleDeleteResponseDto response = scheduleService.deleteSchedule(callScheduleId, memberId);
		return ResponseEntity.ok(CommonResponse.ok("일정이 성공적으로 삭제되었습니다.", response));
	}
}