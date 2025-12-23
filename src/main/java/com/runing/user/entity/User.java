package com.runing.user.entity;

import com.runing.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

	@Column(nullable = false, length = 255)
	private String password; // 비밀번호

	@Column(nullable = false, length = 50)
	private String name; // 실제 이름

	@Column(nullable = false, unique = true, length = 20)
	private String nickname; // 사용할 닉네임

	@Column(nullable = false, length = 20)
	private String phoneNumber; // 전화번호(010-0000-1111)

	@Column(length = 255)
	private String profileUrl; // 프로필 이미지 url

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	@Column(nullable = false)
	private boolean deleted;

	public User(String email, String password, String name, String nickname, String phoneNumber, String profileUrl) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.nickname = nickname;
		this.phoneNumber = phoneNumber;
		this.profileUrl = profileUrl;
		this.role = Role.USER;
		this.deleted = false;
	}

	public void updateProfile(String newName, String newPhoneNumber, String newProfileUrl) {
		if (newName != null && !newName.equals(this.name)){
			this.name = newName;
		}
		if (newPhoneNumber != null && !newPhoneNumber.equals(this.phoneNumber)){
			this.phoneNumber = newPhoneNumber;
		}
		if (newProfileUrl != null && !newProfileUrl.equals(this.profileUrl)){
			this.profileUrl = newProfileUrl;
		}
	}

	/**
	 * 소프트 삭제
	 */
	public void delete() {
		this.deleted = true;
	}
	public void restore() {
		this.deleted = false;
	}
}
