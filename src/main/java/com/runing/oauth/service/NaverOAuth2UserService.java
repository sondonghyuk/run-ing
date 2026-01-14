package com.runing.oauth.service;

import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.runing.jwt.dto.CustomUserDetails;
import com.runing.user.entity.AuthProvider;
import com.runing.user.entity.Profile;
import com.runing.user.entity.User;
import com.runing.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NaverOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final UserRepository userRepository;
	private final DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);
		Map<String, Object> response = oAuth2User.getAttribute("response");

		if (response == null) {
			throw new OAuth2AuthenticationException("Naver OAuth2 response가 없습니다.");
		}

		String email = (String)response.get("email");
		String name = (String)response.get("name");
		String providerId = (String)response.get("id");

		if (email == null || providerId == null) {
			throw new OAuth2AuthenticationException("Naver OAuth2 응답에 email/id가 없습니다.");
		}

		User user = userRepository.findByAuthProviderAndProviderId(AuthProvider.NAVER, providerId)
			.map(this::loginExistingUser)
			.orElseGet(() -> createNaverUser(email, name, providerId));

		return new CustomUserDetails(user);
	}

	private User loginExistingUser(User user) {
		user.updateLastLoginAt();
		return user;
	}

	private User createNaverUser(String email, String name, String providerId) {
		User user = User.createOAuth(email, AuthProvider.NAVER, providerId);

		String safeName = (name == null || name.isBlank())
			? "NaverUser"
			: name.trim();

		String nickname = makeUniqueNickname(safeName);

		Profile profile = new Profile(
			nickname,
			safeName,
			"010-0000-0000",
			null,
			null
		);

		user.attachProfile(profile);
		user.updateLastLoginAt();

		return userRepository.save(user);
	}

	private String makeUniqueNickname(String name) {
		String base = "Runner_" + name;
		String candidate = base;
		int cnt = 0;
		while (userRepository.existsByProfile_Nickname(candidate)) {
			cnt++;
			candidate = base + "_" + cnt;
		}
		return candidate;
	}
}
