package com.runing.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.runing.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	// 이메일 중복 확인
	boolean existsByEmail(String email);

	// 닉네임 중복 확인
	boolean existsByNickname(String nickname);

	// 이메일로 찾기
	Optional<User> findByEmail(String email);
}
