// package com.runing.jwt;
//
// import java.io.IOException;
//
// import org.springframework.web.filter.OncePerRequestFilter;
//
// import com.runing.user.entity.User;
//
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
//
// /**
//  * JWT 를 검증하기 위한 필터
//  */
// @RequiredArgsConstructor
// @Slf4j
// public class JWTFilter extends OncePerRequestFilter {
//
// 	private final JWTUtil jwtUtil;
//
// 	@Override
// 	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
// 		FilterChain filterChain) throws ServletException, IOException {
//
// 		//request 에서 Authorization 헤더 찾음
// 		String authorization = request.getHeader("Authorization");
//
// 		//Authorization 헤더 검증
// 		if (authorization == null || !authorization.startsWith("Bearer ")) {
// 			filterChain.doFilter(request, response);
// 			return;
// 		}
//
// 		String token = authorization.substring(7);
//
// 		//토큰 검증
// 		if(jwtUtil.validateToken(token)) {
// 			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
// 			response.setContentType("application/json");
// 			response.setCharacterEncoding("UTF-8");
// 			response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
// 			return;
// 		}
//
// 		try{
// 			Long userId = jwtUtil.getUserId(token);
// 			String email = jwtUtil.getEmail(token);
// 			String nickname = jwtUtil.getNickname(token);
//
// 			//CustomUserDetails 생성
// 			new CustomUserDetails(new)
// 		}
// 		//토큰에서 정보 획득
// 		String username = jwtUtil.getUsername(token);
// 		String role = jwtUtil.getRole(token);
//
// 		User user = new User(username, role);
//
// 	}
// }
