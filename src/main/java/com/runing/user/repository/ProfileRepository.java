package com.runing.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.runing.user.entity.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
	// 닉네임 중복 확인
	boolean existsByNickname(String nickname);
}
