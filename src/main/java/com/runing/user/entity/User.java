package com.runing.user.entity;
import java.time.LocalDateTime;

import com.runing.common.entity.BaseEntity;
import com.runing.common.error.BaseException;
import com.runing.common.error.UserErrorCode;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name="users",
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_users_provider",columnNames = {"authProvider","providerId"})
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

	@Column(nullable = false, unique = true, length = 255)
	private String email; // 이메일(아이디)

	@Column(nullable = true, length = 60)
	private String password; // 비밀번호

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role; // 회원 역할

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserStatus status; // 회원 상태

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AuthProvider authProvider; // OAuth

	@Column(length = 255)
	private String providerId; // OAuth 고유 ID

	private LocalDateTime lastLoginAt; // 마지막 로그인 시간

	private LocalDateTime deletedAt; // 탈퇴 시점

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Profile profile;

	// Local 로그인
	public static User createLocal(String email, String encodedPassword) {
		User user = new User();
		user.email = email;
		user.password = encodedPassword;
		user.role = Role.USER;
		user.authProvider = AuthProvider.LOCAL;
		user.providerId = null;
		user.status = UserStatus.ACTIVE;
		return user;
	}

	// OAuth 로그인
	public static User createOAuth(String email, AuthProvider authProvider, String providerId) {
		if (providerId == null || providerId.isBlank()) {
			throw new BaseException(UserErrorCode.PROVIDER_ID_REQUIRED);
		}
		if (authProvider == null || authProvider == AuthProvider.LOCAL) {
			throw new BaseException(UserErrorCode.INVALID_AUTH_PROVIDER);
		}
		User user = new User();
		user.email = email;
		user.password = null;
		user.role = Role.USER;
		user.authProvider = authProvider;
		user.providerId = providerId;
		user.status = UserStatus.ACTIVE;
		return user;
	}

	public void attachProfile(Profile profile) {
		if (profile == null) {
			throw new BaseException(UserErrorCode.PROFILE_CANNOT_NULL);
		}
		this.profile = profile;
		profile.attachUser(this);
	}

	// 휴먼 계정 전환
	public void markInactive() {
		if (this.status == UserStatus.WITHDRAWN) {
			throw new BaseException(UserErrorCode.USER_ALREADY_WITHDRAW);
		}
		this.status = UserStatus.INACTIVE;
	}

	//탈퇴 여부 확인
	public boolean isWithdraw(){
		return this.status == UserStatus.WITHDRAWN;
	}

	// 탈퇴
	public void withdraw(){
		if (this.status == UserStatus.WITHDRAWN) {
			throw new BaseException(UserErrorCode.USER_ALREADY_WITHDRAW);
		}
		this.status = UserStatus.WITHDRAWN;
		this.deletedAt = LocalDateTime.now();
	}

	// 마지막 로그인 시간
	public void updateLastLoginAt() {
		if(this.status == UserStatus.WITHDRAWN){
			throw new BaseException(UserErrorCode.USER_ALREADY_WITHDRAW);
		}
		this.lastLoginAt = LocalDateTime.now();
	}

}
