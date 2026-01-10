package com.runing.auth.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RefreshTokenRequest(
	@NotNull UUID userUuid,
	@NotBlank String refreshToken
) {
}
