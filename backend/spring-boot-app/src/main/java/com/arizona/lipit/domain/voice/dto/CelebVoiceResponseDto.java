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
public class CelebVoiceResponseDto {
    @Schema(description = "음성 이름", example = "스윙스")
    private String voiceName;
    
    @Schema(description = "음성 이미지 URL", example = "https://example.com/image.jpg")
    private String customImageUrl;
    
    @Schema(description = "음성 활성화 여부", example = "true")
    private boolean activated;
} 