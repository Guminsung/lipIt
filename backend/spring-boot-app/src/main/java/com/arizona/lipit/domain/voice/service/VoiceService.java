package com.arizona.lipit.domain.voice.service;

import com.arizona.lipit.domain.auth.entity.Member;
import com.arizona.lipit.domain.auth.repository.MemberRepository;
import com.arizona.lipit.domain.voice.dto.SelectVoiceRequestDto;
import com.arizona.lipit.domain.voice.dto.SelectVoiceResponseDto;
import com.arizona.lipit.domain.voice.dto.UserVoiceResponseDto;
import com.arizona.lipit.domain.voice.dto.VoiceResponseDto;
import com.arizona.lipit.domain.voice.dto.RecordingVoiceRequestDto;
import com.arizona.lipit.domain.voice.dto.RecordingVoiceResponseDto;
import com.arizona.lipit.domain.voice.entity.MemberVoice;
import com.arizona.lipit.domain.voice.entity.Voice;
import com.arizona.lipit.domain.voice.entity.VoiceType;
import com.arizona.lipit.domain.voice.mapper.VoiceMapper;
import com.arizona.lipit.domain.voice.repository.MemberVoiceRepository;
import com.arizona.lipit.domain.voice.repository.VoiceRepository;
import com.arizona.lipit.global.exception.CustomException;
import com.arizona.lipit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoiceService {

    private final MemberRepository memberRepository;
    private final MemberVoiceRepository memberVoiceRepository;
    private final VoiceRepository voiceRepository;
    private final VoiceMapper voiceMapper;

    @Transactional(readOnly = true)
    public List<VoiceResponseDto> getCelebVoicesByMemberId(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_USER));

        List<MemberVoice> memberVoices = memberVoiceRepository.findCelebVoicesByMemberId(memberId);
        
        return voiceMapper.toVoiceResponseDtoList(memberVoices);
    }
    
    @Transactional(readOnly = true)
    public List<VoiceResponseDto> getCustomVoicesByMemberId(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_USER));

        List<MemberVoice> memberVoices = memberVoiceRepository.findCustomVoicesByMemberId(memberId);
        
        return voiceMapper.toVoiceResponseDtoList(memberVoices);
    }

    @Transactional(readOnly = true)
    public List<UserVoiceResponseDto> getAllVoicesByMemberId(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_USER));

        return memberVoiceRepository.findSelectedVoiceByMemberId(memberId)
                .map(memberVoice -> Collections.singletonList(voiceMapper.toUserVoiceResponseDto(memberVoice)))
                .orElse(Collections.emptyList());
    }

    @Transactional
    public SelectVoiceResponseDto selectVoice(Long memberId, SelectVoiceRequestDto requestDto) {
        // 사용자 존재 여부 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_USER));
        
        // memberVoiceId 존재 여부 확인 및 사용자의 음성인지 확인
        MemberVoice memberVoice = memberVoiceRepository.findById(requestDto.getMemberVoiceId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "해당 음성을 찾을 수 없습니다."));
        
        if (!memberVoice.getMember().getMemberId().equals(memberId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS, "자신의 음성만 선택할 수 있습니다.");
        }
        
        // 선택한 음성 ID 업데이트
        member.setSelectedVoiceId(memberVoice.getVoice().getVoiceId());
        memberRepository.save(member);
        
        // 응답 DTO 생성
        return SelectVoiceResponseDto.builder()
                .memberId(memberId)
                .selectedVoiceId(memberVoice.getVoice().getVoiceId())
                .voiceName(memberVoice.getVoice().getVoiceName())
                .type(memberVoice.getVoice().getType().toString())
                .build();
    }

    @Transactional
    public RecordingVoiceResponseDto saveRecordingVoice(RecordingVoiceRequestDto requestDto, Long memberId) {
        // 필수 필드 검증
        if (memberId == null || requestDto.getVoiceName() == null || requestDto.getAudioUrl() == null) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "필수 정보가 누락되었습니다.");
        }

        // URL 형식 검증
        try {
            new URL(requestDto.getAudioUrl());
            if (requestDto.getImageUrl() != null) {
                new URL(requestDto.getImageUrl());
            }
        } catch (MalformedURLException e) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "유효하지 않은 URL 형식입니다.");
        }

        // 사용자 존재 여부 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_USER));

        // 중복 음성 이름 확인
        List<MemberVoice> existingVoices = memberVoiceRepository.findAllVoicesByMemberId(memberId);
        boolean isDuplicate = existingVoices.stream()
                .anyMatch(mv -> mv.getVoice().getVoiceName().equals(requestDto.getVoiceName()));
        
        if (isDuplicate) {
            throw new CustomException(ErrorCode.CONFLICT, "이미 존재하는 음성 이름입니다.");
        }

        // 새 Voice 엔티티 생성
        Voice voice = Voice.builder()
                .voiceName(requestDto.getVoiceName())
                .audioUrl(requestDto.getAudioUrl())
                .imageUrl(requestDto.getImageUrl())
                .type(VoiceType.CUSTOM)
                .build();
        
        Voice savedVoice = voiceRepository.save(voice);

        // MemberVoice 연결 엔티티 생성
        MemberVoice memberVoice = MemberVoice.builder()
                .member(member)
                .voice(savedVoice)
                .activate(true)
                .build();
        
        MemberVoice savedMemberVoice = memberVoiceRepository.save(memberVoice);

        // 응답 DTO 생성
        return RecordingVoiceResponseDto.builder()
                .memberId(member.getMemberId())
                .memberVoiceId(savedMemberVoice.getMemberVoiceId())
                .voiceId(savedVoice.getVoiceId())
                .voiceName(savedVoice.getVoiceName())
                .type(savedVoice.getType().toString())
                .imageUrl(savedVoice.getImageUrl())
                .audioUrl(savedVoice.getAudioUrl())
                .build();
    }
}