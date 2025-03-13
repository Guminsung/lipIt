package com.arizona.lipit.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arizona.lipit.domain.auth.dto.MemberDto;
import com.arizona.lipit.domain.auth.dto.SignupRequestDto;
import com.arizona.lipit.domain.auth.service.SignupService;
import com.arizona.lipit.global.docs.auth.SignupApiSpec;
import com.arizona.lipit.global.response.CommonResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/signup")
public class SignupController implements SignupApiSpec {

	private final SignupService signupService;

	@PostMapping
	public ResponseEntity<CommonResponse<MemberDto>> createMember(@Valid @RequestBody SignupRequestDto requestDto) {
		MemberDto userDto = signupService.saveMember(requestDto);
		return ResponseEntity.ok(CommonResponse.created("회원가입이 완료되었습니다.", userDto));
	}
}

