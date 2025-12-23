package com.runing.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
	@NotBlank(message = "이메일은 필수입니다.")
	@Email(message = "유효한 이메일 형식이어야 합니다.")
	@Size(max = 255, message = "이메일은 255자 이하여야 합니다.")
	String email,

	@NotBlank(message = "비밀번호는 필수입니다.")
	@Size(min = 8, max = 60, message = "비밀번호는 8자 이상 60자 이하여야 합니다.")
	@Pattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&*])\\S{8,}$",
		message = "비밀번호는 최소 8자 이상, 숫자, 문자, 특수문자를 포함해야 합니다.")
	String password,

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

	String profileUrl
) {
}
