package com.runing.common.response;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SendErrorResponse {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	// 유틸리티 클래스 인스턴스화 방지
	private SendErrorResponse() {
		throw new UnsupportedOperationException("Utility class");
	}

	// 기존 메서드 - 401 고정 (하위 호환성 유지)
	public static void sendResponse(HttpServletResponse response, String message) {
		sendResponse(response, HttpServletResponse.SC_UNAUTHORIZED, message);
	}

	// 새로운 메서드 - 상태 코드 파라미터화
	public static void sendResponse(HttpServletResponse response, int statusCode, String message) {
		try {
			response.setStatus(statusCode);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			Map<String, Object> errorBody = Map.of(
				"success", false,
				"message", message,
				"data", (Object) null
			);

			response.getWriter().write(objectMapper.writeValueAsString(errorBody));
			response.getWriter().flush();
		} catch (IOException e) {
			log.error("Error response 전송 실패: {}", e.getMessage(), e);
		}
	}
}
