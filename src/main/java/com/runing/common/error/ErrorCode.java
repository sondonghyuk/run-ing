package com.runing.common.error;

import org.springframework.http.HttpStatus;

/**
 * 에러 코드의 형식 통일을 위한 인터페이스
 */
public interface ErrorCode {
	HttpStatus getStatus();
	String getCode();
	String getMessage();
}
