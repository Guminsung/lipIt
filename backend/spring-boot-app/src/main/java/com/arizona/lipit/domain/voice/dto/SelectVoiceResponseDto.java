package com.arizona.lipit.domain.voice.dto;

import com.arizona.lipit.domain.voice.entity.VoiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SelectVoiceResponseDto {
    private Long memberId;
    private Long selectedVoiceId;
    private String voiceName;
    private String type;
} 