package com.arizona.lipit.global.jwt;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 이 필터는 Spring Security에서 HTTP 요청이 들어올 때마다 실행됨
 * OncePerRequestFilter를 상속받았기 때문에 모든 요청마다 한 번씩 실행됨
 * 클라이언트가 API 요청을 보낼 때, JWT 인증을 검사하고 인증 객체를 설정하는 역할을 수행
 * [클라이언트 요청] → [JwtAuthenticationFilter] → [Spring Security 인증 검사] → [Controller 실행]
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		String token = resolveToken(request);  // 헤더에서 토큰 추출

		if (token != null && jwtProvider.validateAccessToken(token)) {
			Authentication authentication = jwtProvider.getAuthentication(token);
			SecurityContextHolder.getContext()
				.setAuthentication(authentication);  // 검증이 성공하면 SecurityContextHolder에 인증 객체 저장
		}

		// 필터 체인을 통해 다음 필터 또는 컨트롤러로 요청 전달
		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);  // "Bearer " 제거 후 반환
		}
		return null;
	}
}
