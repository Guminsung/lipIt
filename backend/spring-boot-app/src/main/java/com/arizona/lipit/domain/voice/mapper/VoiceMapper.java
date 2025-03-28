package com.arizona.lipit.domain.voice.mapper;

import com.arizona.lipit.domain.auth.entity.Member;
import com.arizona.lipit.domain.voice.dto.CelebVoiceResponseDto;
import com.arizona.lipit.domain.voice.dto.UserVoiceResponseDto;
import com.arizona.lipit.domain.voice.dto.VoiceResponseDto;
import com.arizona.lipit.domain.voice.entity.MemberVoice;
import com.arizona.lipit.domain.voice.entity.Voice;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VoiceMapper {

    /**
     * Voice 엔티티를 VoiceResponseDto로 변환
     */
    public VoiceResponseDto toVoiceResponseDto(Voice voice) {
        return VoiceResponseDto.builder()
                .voiceName(voice.getVoiceName())
                .customImageUrl(voice.getImageUrl())
                .build();
    }

    /**
     * Voice 엔티티를 CelebVoiceResponseDto로 변환 (리포트 카운트 반영)
     */
    public CelebVoiceResponseDto toCelebVoiceResponseDto(Voice voice, boolean activated) {
        return CelebVoiceResponseDto.builder()
                .voiceName(voice.getVoiceName())
                .customImageUrl(voice.getImageUrl())
                .activated(activated)
                .build();
    }

    /**
     * MemberVoice 엔티티를 VoiceResponseDto로 변환
     */
    public VoiceResponseDto toVoiceResponseDto(MemberVoice memberVoice) {
        return VoiceResponseDto.builder()
                .voiceName(memberVoice.getVoice().getVoiceName())
                .customImageUrl(memberVoice.getVoice().getImageUrl())
                .build();
    }

    /**
     * MemberVoice 엔티티 리스트를 VoiceResponseDto 리스트로 변환
     */
    public List<VoiceResponseDto> toVoiceResponseDtoList(List<MemberVoice> memberVoices) {
        return memberVoices.stream()
                .map(this::toVoiceResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * MemberVoice 엔티티를 UserVoiceResponseDto로 변환
     */
    public UserVoiceResponseDto toUserVoiceResponseDto(MemberVoice memberVoice) {
        return UserVoiceResponseDto.builder()
                .voiceName(memberVoice.getVoice().getVoiceName())
                .customImageUrl(memberVoice.getVoice().getImageUrl())
                .build();
    }

    /**
     * Voice 엔티티를 UserVoiceResponseDto로 변환
     */
    public UserVoiceResponseDto toUserVoiceResponseDto(Voice voice) {
        return UserVoiceResponseDto.builder()
                .voiceName(voice.getVoiceName())
                .customImageUrl(voice.getImageUrl())
                .build();
    }

    /**
     * MemberVoice 엔티티 리스트를 UserVoiceResponseDto 리스트로 변환
     */
    public List<UserVoiceResponseDto> toUserVoiceResponseDtoList(List<MemberVoice> memberVoices) {
        return memberVoices.stream()
                .map(this::toUserVoiceResponseDto)
                .collect(Collectors.toList());
    }
}
