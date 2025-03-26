package com.arizona.lipit.domain.voice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecordingVoiceRequestDto {
    private Long memberId;
    private String voiceName;
    private String audioUrl;
    private String imageUrl;
} 