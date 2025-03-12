package com.arizona.lipit.domain.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arizona.lipit.domain.auth.dto.MemberDto;
import com.arizona.lipit.domain.auth.dto.SignupRequestDto;
import com.arizona.lipit.domain.auth.entity.Member;
import com.arizona.lipit.domain.auth.mapper.MemberMapper;
import com.arizona.lipit.domain.auth.repository.MemberRepository;
import com.arizona.lipit.global.exception.CustomException;
import com.arizona.lipit.global.util.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SignupService {

	private final MemberRepository memberRepository;
	private final MemberMapper memberMapper;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public MemberDto saveMember(SignupRequestDto request) {
		// 이메일 중복 체크
		if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
		}

		// 비밀번호 일치 여부 체크
		if (!request.getPassword1().equals(request.getPassword2())) {
			throw new CustomException(ErrorCode.PASSWORDS_DO_NOT_MATCH);
		}

		// 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(request.getPassword1());

		// 새로운 사용자 생성 및 저장
		// 비밀번호 암호화 후 Member 엔티티 생성
		Member member = Member.builder()
			.email(request.getEmail())
			.password(passwordEncoder.encode(request.getPassword1()))
			.username(request.getUsername())
			.build();

		// DB 저장
		memberRepository.save(member);

		// 엔티티 → DTO 변환 후 반환
		return memberMapper.toDto(member);
	}
}
