package com.arizona.lipit.domain.voice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoiceResponseDto {
    private String voiceName;
    private String customImageUrl;
    private Boolean activate;
}