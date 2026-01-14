package com.runing.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
@ConfigurationProperties(prefix = "jwt.cookie")
public class JwtCookieProperties {

	private boolean secure;
	private String sameSite;
	private boolean httpOnly;
	private long maxAgeDays;
}