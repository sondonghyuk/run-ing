package com.runing.user.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.runing.common.error.BaseException;
import com.runing.common.error.UserErrorCode;
import com.runing.user.dto.ProfileDto;
import com.runing.user.dto.UserCreateRequest;
import com.runing.user.dto.UserDto;
import com.runing.user.dto.UserUpdateRequest;
import com.runing.user.entity.Profile;
import com.runing.user.entity.User;
import com.runing.user.mapper.ProfileMapper;
import com.runing.user.mapper.UserMapper;
import com.runing.user.repository.ProfileRepository;
import com.runing.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
	private final UserRepository userRepository;
	private final ProfileRepository profileRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserMapper userMapper;
	private final ProfileMapper profileMapper;
	private final ProfileImageService profileImageService;

	// 회원가입
	@Transactional
	public UserDto createUser(UserCreateRequest request) {
		log.debug("사용자 생성 시작: email={}", request.email());

		// 중복검증
		validateDuplicateUser(request.email(), request.nickname());

		// 유저 생성
		String hashedPassword = passwordEncoder.encode(request.password());
		User user = User.createLocal(request.email(), hashedPassword);
		Profile profile = new Profile(
			request.nickname(),
			request.name(),
			request.phoneNumber(),
			request.profileUrl(),
			request.region()
		);
		user.attachProfile(profile);

		// 저장
		User savedUser = userRepository.save(user);
		log.info("사용자 생성 완료 : id={} , email={}, username={}", savedUser.getId(), savedUser.getEmail(),
			savedUser.getProfile().getName());

		// userDto 변환 후 반환
		return userMapper.toDto(savedUser);
	}

	// 마이페이지 조회
	public ProfileDto getMyProfile(UUID userUuid){
		// 유저 찾기
		User user = userRepository.findByUuid(userUuid)
			.orElseThrow(() -> new BaseException(UserErrorCode.USER_NOT_FOUND));

		// 프로필 검증
		if (user.getProfile() == null) {
			throw new BaseException(UserErrorCode.PROFILE_NOT_FOUND);
		}

		return profileMapper.toDto(user);
	}


	// 회원정보 수정
	@Transactional
	public ProfileDto updateProfile(UUID userUuid, UserUpdateRequest request) {
		// 유저 찾기
		User user = userRepository.findByUuid(userUuid)
			.orElseThrow(() -> new BaseException(UserErrorCode.USER_NOT_FOUND));

		Profile profile = user.getProfile();
		if (profile == null) {
			throw new BaseException(UserErrorCode.PROFILE_NOT_FOUND);
		}

		// 닉네임 변경 시 중복 검증
		validateNicknameChange(user,request.nickname());

		// 회원정보 수정
		profile.update(
			request.nickname(),
			request.name(),
			request.phoneNumber(),
			request.profileUrl(),
			request.region()
		);

		log.info("프로필 업데이트 완료 : userId={}, email={}", userUuid, user.getEmail());

		return profileMapper.toDto(user);
	}

	// 이메일, 닉네임 중복 검증
	private void validateDuplicateUser(String email, String nickname) {
		if (userRepository.existsByEmail(email)) {
			throw new BaseException(UserErrorCode.USER_EMAIL_EXISTS);
		}
		if (profileRepository.existsByNickname(nickname)) {
			throw new BaseException(UserErrorCode.USER_NICKNAME_EXISTS);
		}
	}

	// 닉네임 중복 검증
	private void validateNicknameChange(User user,String newNickname) {
		// 기존 닉네임과 같으면 검증 불필요
		if (newNickname.equals(user.getProfile().getNickname())) return;

		// 중복 검증
		if (profileRepository.existsByNickname(newNickname)) {
			throw new BaseException(UserErrorCode.USER_NICKNAME_EXISTS);
		}
	}

	// 이메일 중복 확인 (API 용도)
	@Transactional(readOnly = true)
	public void checkDuplicateEmail(String email) {
		if(userRepository.existsByEmail(email)) {
			throw new BaseException(UserErrorCode.USER_EMAIL_EXISTS);
		}
	}

	// 닉네임 중복 확인 (API 용도)
	@Transactional(readOnly = true)
	public void checkDuplicateNickname(String nickname) {
		if (profileRepository.existsByNickname(nickname)) {
			throw new BaseException(UserErrorCode.USER_NICKNAME_EXISTS);
		}
	}

	// 유저 찾기
	public UserDto findByUuid(UUID userUuid) {
		return userRepository.findByUuid(userUuid)
			.map(userMapper::toDto)
			.orElseThrow(() -> new BaseException(UserErrorCode.USER_NOT_FOUND));
	}

	// 프로필 이미지 업로드
	@Transactional
	public String updateProfileImage(UUID userUuid, MultipartFile file){
		User user = userRepository.findByUuid(userUuid)
			.orElseThrow(() -> new BaseException(UserErrorCode.USER_NOT_FOUND));

		Profile profile = user.getProfile();
		if (profile == null) {
			throw new BaseException(UserErrorCode.PROFILE_NOT_FOUND);
		}

		String oldImageUrl = profile.getProfileUrl();
		String newImageUrl = profileImageService.store(file,userUuid,oldImageUrl);

		profile.updateProfileUrl(newImageUrl);

		return newImageUrl;
	}

	// 탈퇴 : 논리삭제
	@Transactional
	public void withdraw(UUID userUuid) {
		User user = userRepository.findByUuid(userUuid)
			.orElseThrow(() -> new BaseException(UserErrorCode.USER_NOT_FOUND));

		user.withdraw();
	}
}
