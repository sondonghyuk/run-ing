package com.runing.common.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.runing.common.response.ErrorResponse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

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

	// @Valid 검증 실패 처리
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
		String message = ex.getBindingResult()
			.getAllErrors()
			.get(0)
			.getDefaultMessage();

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(new ErrorResponse(
				HttpStatus.BAD_REQUEST.value(),
				"VALIDATION_ERROR",
				message
			));
	}

	// @RequestParam, @PathVariable 검증 실패 처리
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolation(
		ConstraintViolationException ex
	) {
		String message = ex.getConstraintViolations()
			.stream()
			.findFirst()
			.map(ConstraintViolation::getMessage)
			.orElse("잘못된 요청입니다");

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(new ErrorResponse(
				HttpStatus.BAD_REQUEST.value(),
				"VALIDATION_ERROR",
				message
			));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception ex){
		ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
		return ResponseEntity
			.status(errorCode.getStatus())
			.body(new ErrorResponse(errorCode.getStatus().value(), errorCode.getCode(), errorCode.getMessage()));
	}
}
