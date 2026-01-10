package com.runing.jwt.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.runing.user.entity.Role;
import com.runing.user.entity.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

	private final User user;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new GrantedAuthority() {
			@Override
			public @Nullable String getAuthority() {
				return user.getRole().toString();
			}
		});
		return authorities;
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

	public @Nullable UUID getUserUuid(){
		return user.getUuid();
	}
	public @Nullable String getEmail(){
		return user.getEmail();
	}
	public @Nullable String getName(){
		return user.getProfile().getName();
	}
	public @Nullable Role getRole(){
		return user.getRole();
	}
}
