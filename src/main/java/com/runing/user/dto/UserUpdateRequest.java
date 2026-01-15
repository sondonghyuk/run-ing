package com.runing.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
	@NotBlank(message = "닉네임은 필수입니다.")
	@Size(max = 20, message = "닉네임은 20자 이하여야 합니다.")
	String nickname,

	@NotBlank(message = "사용자 이름은 필수입니다.")
	@Size(min = 3, max = 50, message = "사용자 이름은 3자 이상 50자 이하입니다.")
	String name,

	//010-1234-5678 , +82-10-1234-5678
	@NotBlank(message = "전화번호는 필수입니다.")
	@Pattern(regexp = "^(010-\\d{4}-\\d{4}|\\+82-10-\\d{4}-\\d{4})$")
	String phoneNumber,

	String profileUrl,

	String region
) {
}
