package com.runing.common.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.runing.common.response.ErrorResponse;

/**
 * @RestController 로 선언한 지점에서 발생한 에러를 도중에
 * @RestControllerAdvice 로 선언한 클래스 내에서 캐치하여
 * Controller 내에서 발생한 에러를 처리
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex){
		ErrorCode errorCode = ex.getErrorCode();
		return ResponseEntity
			.status(errorCode.getStatus())
			.body(new ErrorResponse(errorCode.getStatus().value(), errorCode.getCode(), errorCode.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception ex){
		ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
		return ResponseEntity
			.status(errorCode.getStatus())
			.body(new ErrorResponse(errorCode.getStatus().value(), errorCode.getCode(), errorCode.getMessage()));
	}
}
