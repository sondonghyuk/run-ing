package com.runing.jwt.dto;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.runing.user.entity.Role;
import com.runing.user.entity.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails, OAuth2User {

	private final User user;
	private Map<String, Object> attributes = Map.of();

	@Override
	public Map<String,Object> getAttributes() {
		return attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(
			new SimpleGrantedAuthority(user.getRole().toString())
		);
	}


	@Override
	public @Nullable String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail(); // email 을 username 으로 사용
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public UUID getUserUuid(){
		return user.getUuid();
	}
	public String getEmail(){
		return user.getEmail();
	}
	public String getName(){
		// Profile 자체가 null 인지 체크
		if (user.getProfile() == null) {
			return user.getEmail();
		}
		// Profile 은 있지만 name 은 null 인 경우
		return user.getProfile().getName() != null ?  user.getProfile().getName() : user.getEmail();
	}
	public Role getRole(){
		return user.getRole();
	}
}
