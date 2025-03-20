package com.arizona.lipit.domain.auth.service;

import org.springframework.stereotype.Service;

import com.arizona.lipit.domain.auth.dto.MemberDto;
import com.arizona.lipit.domain.auth.entity.Member;
import com.arizona.lipit.domain.auth.mapper.MemberMapper;
import com.arizona.lipit.domain.auth.repository.MemberRepository;
import com.arizona.lipit.global.exception.CustomException;
import com.arizona.lipit.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final MemberMapper memberMapper;

	public MemberDto getMemberById(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_ID_NOT_FOUND));

		return memberMapper.toDto(member);
	}
}
