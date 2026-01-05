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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

	@Column(nullable = false, unique = true, length = 255)
	private String email; // 이메일(아이디)

	@Column(nullable = false, length = 60)
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

	private LocalDateTime lastLoginAt; // 마지막 로그인 시간

	private LocalDateTime deletedAt; // 탈퇴 시점

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Profile profile;

	public User(String email, String password) {
		this.email = email;
		this.password = password;
		this.role = Role.USER;
		this.authProvider = AuthProvider.LOCAL;
		this.status = UserStatus.ACTIVE;
	}

	// OAuth 로그인용 생성자
	public User(String email, AuthProvider authProvider) {
		this.email = email;
		this.password = "OAUTH"; // OAuth는 비밀번호 불필요
		this.authProvider = authProvider;
		this.role = Role.USER;
		this.status = UserStatus.ACTIVE;
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
		this.status = UserStatus.WITHDRAWN;
		this.deletedAt = LocalDateTime.now();
	}

	// 마지막 로그인 시간
	public void updateLastLoginAt() {
		this.lastLoginAt = LocalDateTime.now();
	}

}
