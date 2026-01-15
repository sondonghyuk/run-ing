package com.runing.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
	private String baseUrl;
	private Email email = new Email();

	@Getter
	@Setter
	public static class Email {
		private Verification verification = new Verification();

		@Getter
		@Setter
		public static class Verification {
			private int expirationMinutes = 10;
			private int rateLimitMinutes = 1;
		}
	}
}
