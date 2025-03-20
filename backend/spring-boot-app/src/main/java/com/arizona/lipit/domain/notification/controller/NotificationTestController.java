package com.arizona.lipit.domain.notification.controller;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arizona.lipit.domain.notification.dto.NotificationRequestDto;
import com.arizona.lipit.domain.notification.service.NotificationService;
import com.arizona.lipit.global.docs.notification.NotificationTestApiSpec;
import com.arizona.lipit.global.response.CommonResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationTestController implements NotificationTestApiSpec {

	private final NotificationService notificationService;

	@PostMapping
	public ResponseEntity<CommonResponse<Map<String, String>>> sendTestNotification(
		@RequestBody NotificationRequestDto requestDto) throws IOException, ExecutionException, InterruptedException {
		Map<String, String> responseData = notificationService.sendNotificationToUser(requestDto);
		return ResponseEntity.ok(CommonResponse.created("알림이 성공적으로 전송되었습니다.", responseData));
	}
}
