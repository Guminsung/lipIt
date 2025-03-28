package com.arizona.lipit.domain.voice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CelebVoiceResponseDto {
    private String voiceName;
    private String customImageUrl;
    private boolean activated;
} 