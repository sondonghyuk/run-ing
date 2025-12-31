package com.runing.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.runing.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	// 이메일 중복 확인
	boolean existsByEmail(String email);

	// 이메일로 찾기
	Optional<User> findByEmail(String email);

	// 이름으로 찾기
	Optional<User> findByUsername(String username);
}
