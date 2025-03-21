package com.arizona.lipit.domain.onboarding.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arizona.lipit.domain.auth.dto.MemberDto;
import com.arizona.lipit.domain.auth.entity.Member;
import com.arizona.lipit.domain.auth.repository.MemberRepository;
import com.arizona.lipit.domain.onboarding.dto.CallScheduleRequestDto;
import com.arizona.lipit.domain.onboarding.dto.CallScheduleResponseDto;
import com.arizona.lipit.domain.onboarding.dto.UserInterestRequestDto;
import com.arizona.lipit.domain.onboarding.service.CallScheduleService;
import com.arizona.lipit.domain.onboarding.service.UserInterestService;
import com.arizona.lipit.global.docs.onboarding.OnboardingApiSpec;
import com.arizona.lipit.global.exception.CustomException;
import com.arizona.lipit.global.exception.ErrorCode;
import com.arizona.lipit.global.response.CommonResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/onboarding")
public class OnboardingController implements OnboardingApiSpec {

	private final CallScheduleService callScheduleService;
	private final UserInterestService userInterestService;
	private final MemberRepository memberRepository; // 추가

	@PostMapping("/alarm")
	public ResponseEntity<CommonResponse<CallScheduleResponseDto>> createCallSchedule(
		@AuthenticationPrincipal UserDetails userDetails,
		@Valid @RequestBody CallScheduleRequestDto requestDto) {

		String email = userDetails.getUsername(); // 이메일 추출
		Long memberId = memberRepository.findByEmail(email)
			.map(Member::getMemberId)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

		CallScheduleResponseDto responseDto = callScheduleService.saveCallSchedule(memberId, requestDto);

		return ResponseEntity.ok(CommonResponse.created("일정이 성공적으로 저장되었습니다.", responseDto));
	}

	@PostMapping("/interesting")
	public ResponseEntity<CommonResponse<MemberDto>> createUserInterest(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestBody UserInterestRequestDto requestDto) {

		String email = userDetails.getUsername(); // 이메일 추출
		Long memberId = memberRepository.findByEmail(email)
			.map(Member::getMemberId)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

		MemberDto memberDto = userInterestService.saveUserInterest(memberId, requestDto);

		return ResponseEntity.ok(CommonResponse.created("관심 사항이 성공적으로 저장되었습니다.", memberDto));
	}
}
