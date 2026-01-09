package com.runing.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.runing.user.entity.EmailVerification;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
	Optional<EmailVerification> findByEmail(String email);
	boolean existsByEmailAndVerifiedIsTrue(String email);
}
