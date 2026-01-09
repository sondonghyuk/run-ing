package com.runing.user.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.runing.common.error.BaseException;
import com.runing.common.error.UserErrorCode;
import com.runing.user.entity.EmailVerification;
import com.runing.user.repository.EmailVerificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {
	private final RedisTemplate<String, String> redisTemplate;
	private final JavaMailSender mailSender;
	private final EmailVerificationRepository emailVerificationRepository;

	// 이메일 인증시 호출
	public void sendVerificationEmail(String email) {
		log.info("이메일 인증 요청: email={}", email);

		// 이미 인증된 이메일인지 체크
		emailVerificationRepository.findByEmail(email)
			.filter(EmailVerification::isVerified)
			.ifPresent(emailVerification -> {
				throw new BaseException(UserErrorCode.EMAIL_ALREADY_VERIFIED);
			});

		// 호출 제한 토큰 생성
		String limitKey = "email:limit:"+email;

		// 호출 제한 체크
		if(Boolean.TRUE.equals(redisTemplate.hasKey(limitKey))) {
			throw new BaseException(UserErrorCode.EMAIL_SEND_TOO_FREQUENT);
		}

		// 인증 토큰 생성
		String token = UUID.randomUUID().toString();
		String redisKey = "email:verify:"+token;

		// Redis 저장
		redisTemplate.opsForValue().set(redisKey,email,10, TimeUnit.MINUTES);

		// 인증 링크 생성
		String link = "http://localhost:8080/email/verify?token="+token;

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("[Run-ing] 이메일 인증");
		message.setText(
			"안녕하세요,\n\n" +
				"Run-ing 이메일 인증을 위해 아래 링크를 클릭해주세요.\n\n" +
				link + "\n\n" +
				"이 링크는 10분간 유효합니다.\n" +
				"본인이 요청하지 않았다면 이 메일을 무시해주세요."
		);

		try{
			mailSender.send(message);
			// 호출 제한 토큰 저장 -> 이 키가 존재하는 동안 재발송 불가 (1분)
			redisTemplate.opsForValue().set(limitKey,"1",1,TimeUnit.MINUTES);
			log.info("이메일 발송 성공: email={}", email);
		}catch (RuntimeException e) {
			redisTemplate.delete(redisKey); // 발송 실패시 토큰 제거
			log.error("메일 발송 실패 : email={}, error={}", email, e.getMessage());
			throw new BaseException(UserErrorCode.EMAIL_SEND_FAIL);
		}
	}

	// 사용자가 인증 링크를 클릭했을 때 호출
	@Transactional
	public boolean verifyEmail(String token) {
		log.info("이메일 인증 시도: token={}", token);

		// Redis 에서 토큰 조회
		String redisKey = "email:verify:"+token;
		String email = redisTemplate.opsForValue().getAndDelete(redisKey);

		if (email == null) {
			log.warn("유효하지 않은 토큰: token={}", token);
			return false;
		}

		// 이미 인증된 이메일인지 체크
		EmailVerification emailVerification = emailVerificationRepository
			.findByEmail(email)
			.orElseGet(() -> new EmailVerification(email));

		if (emailVerification.isVerified()) {
			log.info("이미 인증된 이메일: email={}", email);
			return true;
		}

		emailVerification.markVerified();
		emailVerificationRepository.save(emailVerification);

		log.info("이메일 인증 완료: email={}", email);

		return true;
	}
}
