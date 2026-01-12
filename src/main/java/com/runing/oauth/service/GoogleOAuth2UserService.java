package com.runing.oauth.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.runing.jwt.dto.CustomUserDetails;
import com.runing.user.entity.AuthProvider;
import com.runing.user.entity.Profile;
import com.runing.user.entity.User;
import com.runing.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoogleOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final UserRepository userRepository;
	private final DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);

		String email = oAuth2User.getAttribute("email");
		String name = oAuth2User.getAttribute("name");
		String providerId = oAuth2User.getAttribute("sub");

		if (email == null || providerId == null) {
			throw new OAuth2AuthenticationException("Google OAuth2 응답에 email/sub가 없습니다.");
		}

		User user = userRepository.findByAuthProviderAndProviderId(AuthProvider.GOOGLE,providerId)
			.map(this::loginExistingUser)
			.orElseGet(() -> createGoogleUser(email, name,providerId));

		return new CustomUserDetails(user);
	}

	private User loginExistingUser(User user) {
		user.updateLastLoginAt();
		return user;
	}

	private User createGoogleUser(String email, String name, String providerId) {
		User user = User.createOAuth(email, AuthProvider.GOOGLE,providerId);

		String safeName = (name == null || name.isBlank()) ? "GoogleUser" : name.trim();
		String nickname = makeUniqueNickname(safeName);

		Profile profile = new Profile(nickname, safeName, "010-0000-0000", null, null);

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
