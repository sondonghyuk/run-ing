package com.runing.user.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.runing.common.error.BaseException;
import com.runing.common.error.UserErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileImageService {

	private static final String PROFILE_IMAGE_DIR = "uploads/profile-images";
	private static final String BASE_URL = "/images/profile-images/";
	private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; //5MB

	@Value("${custom.user.default-profile-image}")
	private String defaultProfileImage;

	public String store(MultipartFile file, UUID userUuid, String oldImageUrl){
		// 파일 검증
		validateFile(file);

		// 기존 파일 삭제
		if(oldImageUrl != null && !oldImageUrl.isEmpty() && !isDefaultImage(oldImageUrl)) {
			deleteFile(oldImageUrl);
		}

		// 파일명 생성
		String extension = getFileExtension(file.getOriginalFilename());
		String fileName = userUuid + "_" + System.currentTimeMillis() + extension;

		// 파일 저장
		try{
			Path dirPath = Paths.get(PROFILE_IMAGE_DIR).toAbsolutePath();
			if(!Files.exists(dirPath)){
				Files.createDirectories(dirPath);
			}

			Path filePath = dirPath.resolve(fileName);
			try(InputStream in  = file.getInputStream()){
				Files.copy(in,filePath, StandardCopyOption.REPLACE_EXISTING);
			}
			log.info("프로필 이미지 저장 성공: userUuid={}, fileName={}", userUuid, fileName);
			return BASE_URL + fileName;
		}catch (IOException e){
			log.error("프로필 이미지 저장 실패: userUuid={}", userUuid, e);
			throw new BaseException(UserErrorCode.FILE_UPLOAD_FAILED);
		}
	}

	// 파일 유효성 검증
	private void validateFile(MultipartFile file) {
		// null 체크
		if (file == null || file.isEmpty()) {
			throw new BaseException(UserErrorCode.FILE_IS_EMPTY);
		}

		// 확장자 검사
		String contentType = file.getContentType();
		if (contentType == null || !contentType.startsWith("image/")) {
			throw new BaseException(UserErrorCode.UPLOAD_ONLY_IMAGE);
		}

		// 파일 크기 제한
		if (file.getSize() > MAX_FILE_SIZE) {
			throw new BaseException(UserErrorCode.UPLOAD_IMAGE_SIZE_UNDER_5MB);
		}
	}

	// 기존 이미지 여부 확인
	private boolean isDefaultImage(String imageUrl) {
		return defaultProfileImage.equals(imageUrl);
	}

	// 기존 파일 삭제
	private void deleteFile(String oldImageUrl) {
		try{
			String fileName = oldImageUrl.replace(BASE_URL,"");
			Path filePath = Paths.get(PROFILE_IMAGE_DIR).toAbsolutePath().resolve(fileName);

			if(Files.exists(filePath)){
				Files.delete(filePath);
				log.info("기존 프로필 이미지 삭제 성공: {}", fileName);
			}
		}catch (IOException e){
			log.warn("기존 프로필 이미지 삭제 실패: {}", oldImageUrl, e);
		}
	}

	// 파일 확장자 추출
	private String getFileExtension(String filename) {
		if (filename == null || filename.isEmpty()) {
			return ".jpg"; // 기본 확장자
		}

		int lastDotIndex = filename.lastIndexOf(".");
		if (lastDotIndex == -1) {
			return ".jpg";
		}

		return filename.substring(lastDotIndex).toLowerCase();
	}
}
