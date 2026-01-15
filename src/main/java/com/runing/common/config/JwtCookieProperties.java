package com.runing.common.config;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.ResponseCookie;

import lombok.Getter;

@Getter
@ConfigurationProperties(prefix = "spring.jwt.cookie")
public class JwtCookieProperties {

	private final boolean secure;
	private final String sameSite;
	private final boolean httpOnly;
	private final long maxAgeDays;

	public JwtCookieProperties(
		@DefaultValue("true") boolean secure,
		@DefaultValue("Strict") String sameSite,
		@DefaultValue("true") boolean httpOnly,
		@DefaultValue("7") long maxAgeDays) {
		this.secure = secure;
		this.sameSite = sameSite;
		this.httpOnly = httpOnly;
		this.maxAgeDays = maxAgeDays;
	}

	public ResponseCookie createCookie(String name, String value) {
		return ResponseCookie.from(name, value)
			.httpOnly(this.httpOnly)
			.secure(this.secure)
			.path("/")
			.maxAge(TimeUnit.DAYS.toSeconds(this.maxAgeDays))
			.sameSite(this.sameSite)
			.build();
	}
}