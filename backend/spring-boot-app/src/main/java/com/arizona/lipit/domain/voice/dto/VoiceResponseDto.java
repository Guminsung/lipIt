package com.arizona.lipit.domain.voice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoiceResponseDto {
    @Schema(description = "음성 ID", example = "1")
    private Long voiceId;
    
    @Schema(description = "음성 이름", example = "내 목소리1")
    private String voiceName;
    
    @Schema(description = "음성 이미지 URL", example = "https://example.com/image.jpg")
    private String customImageUrl;
    
    @Schema(description = "음성 파일 URL", example = "https://example.com/audio.mp3", nullable = false)
    private String audioUrl;
}