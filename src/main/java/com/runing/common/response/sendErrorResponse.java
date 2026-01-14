package com.runing.common.response;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

public class sendErrorResponse {

	public static void sendResponse(HttpServletResponse response, String message) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write("{\"error\": \"" + message + "\"}");
	}
}
