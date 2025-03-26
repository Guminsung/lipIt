package com.arizona.lipit.domain.voice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecordingVoiceResponseDto {
    private Long memberId;
    private Long memberVoiceId;
    private Long voiceId;
    private String voiceName;
    private String type;
    private String imageUrl;
    private String audioUrl;
} 