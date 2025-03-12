package com.arizona.lipit.domain.auth.dto;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberDto {
	@Schema(description = "사용자 ID", example = "1")
	private Long userId;

	@Schema(description = "사용자 이메일", example = "test@test.com")
	private String email;

	@Schema(description = "사용자 이름", example = "test")
	private String username;

	@Schema(description = "생성일", example = "")
	private Timestamp createdAt;
}
