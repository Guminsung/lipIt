package com.arizona.lipit.global.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

	private Info apiInfo() {
		return new Info()
			.title("[Lip It] REST API")
			.description("SSAFY 특화 AI 영상 도메인 프로젝트 **Lip It 서비스의 API 명세서**입니다.")
			.version("v1.0")
			.contact(new Contact().name("Team Arizona")
				.email("www.arizona.com")
				.url("suhmiji@gmail.com"))
			.license(new License()
				.name("License of API")
				.url("API license URL"));
	}

	private SecurityScheme createApiKeyScheme() {
		return new SecurityScheme().type(SecurityScheme.Type.HTTP)
			.bearerFormat("JWT")
			.scheme("bearer");
	}

	@Bean
	public OpenAPI openApi() {
		return new OpenAPI()
			.addServersItem(new Server().url("/api"))
			.addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
			.components(new Components().addSecuritySchemes("Bearer Authentication", createApiKeyScheme()))
			.info(apiInfo());
	}

	@Bean
	public GroupedOpenApi userApi() {
		return GroupedOpenApi.builder()
			.group("1. 회원 관리")
			.pathsToMatch("/auth/**")
			.build();
	}

	@Bean
	public GroupedOpenApi studyApi() {
		return GroupedOpenApi.builder()
			.group("2. 영어 학습")
			.pathsToMatch("/shadowing")
			.build();
	}
}
