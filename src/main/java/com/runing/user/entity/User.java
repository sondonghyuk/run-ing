package com.runing.user.entity;

import com.runing.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

	@Column(nullable = false, unique = true, length = 255)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false, length = 20)
	private String name;

	@Column(nullable = false, unique = true, length = 20)
	private String nickname;

	@Column(name = "phone_number", nullable = false, length = 20)
	private String phoneNumber;

	@Column(name = "profile_url")
	private String profileUrl;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted = false;

	public User(String email, String password, String name, String nickname, String phoneNumber, String profileUrl) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.nickname = nickname;
		this.phoneNumber = phoneNumber;
		this.profileUrl = profileUrl;
		this.role = Role.USER;
	}

	public void changeUsername(String newUsername) {
		this.name = newUsername;
	}
	public void changeUserNickname(String newNickname) {
		this.nickname = newNickname;
	}
	public void changePassword(String encodePassword) {
		this.password = encodePassword;
	}
	public void changePhoneNumber(String newPhoneNumber) {
		this.phoneNumber = newPhoneNumber;
	}
	public void changeProfileUrl(String newProfileUrl) {
		this.profileUrl = newProfileUrl;
	}
	public void changeRole(Role newRole) {
		this.role = newRole;
	}
	public void changeIsDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
}
