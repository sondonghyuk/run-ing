package com.runing.common.error;

/**
 * 에러 코드의 형식 통일을 위한 인터페이스
 */
public interface ErrorCode {
	int getStatus();
	String getCode();
	String getMessage();
}
