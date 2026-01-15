package com.runing.user.entity;

import java.time.LocalDateTime;

import com.runing.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerification extends BaseEntity {

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private boolean verified;

	private LocalDateTime verifiedAt;

	public EmailVerification(String email){
		this.email = email;
		this.verified = false;
	}

	public void markVerified(){
		if (this.verified) {
			return;
		}
		this.verified = true;
		this.verifiedAt = LocalDateTime.now();
	}
}
