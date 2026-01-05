package com.runing.user.entity;

import com.runing.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile extends BaseEntity {

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@Column(nullable = false, length = 50)
	private String name; // 실제 이름

	@Column(nullable = false, unique = true, length = 30)
	private String nickname; // 화면 표시용

	@Column(nullable = false, length = 20)
	private String phoneNumber; // 전화번호(010-0000-1111)

	@Column(length = 255)
	private String profileUrl; // 프로필 이미지 url

	@Column(length = 50)
	private String region;

	public Profile(String nickname, String name, String phoneNumber, String profileUrl, String region) {
		this.nickname = nickname;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.profileUrl = profileUrl;
		this.region = region;
	}

	void attachUser(User user) {
		this.user = user;
	}

	public void update(String newNickname, String newName, String newPhoneNumber, String newProfileUrl,
		String newRegion) {
		if (newNickname != null && !newNickname.equals(this.nickname))
			this.nickname = newNickname;
		if (newName != null && !newName.equals(this.name))
			this.name = newName;
		if (newPhoneNumber != null && !newPhoneNumber.equals(this.phoneNumber))
			this.phoneNumber = newPhoneNumber;
		if (newProfileUrl != null && !newProfileUrl.equals(this.profileUrl))
			this.profileUrl = newProfileUrl;
		if (newRegion != null && !newRegion.equals(this.region))
			this.region = newRegion;
	}
}
