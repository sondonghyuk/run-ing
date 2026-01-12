package com.runing.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.runing.user.entity.AuthProvider;
import com.runing.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	// 이메일 중복 확인
	boolean existsByEmail(String email);

	// 이메일로 조회
	Optional<User> findByEmail(String email);

	// UUID로 조회
	Optional<User> findByUuid(UUID uuid);

	boolean existsByProfile_Nickname(String name);

	Optional<User> findByAuthProviderAndProviderId(AuthProvider authProvider, String providerId);
}
