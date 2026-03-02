package com.example.user_mgmt.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class CustomUserDetails implements UserDetails {

	// uid is stored as String to be generic; caller uses String.valueOf(user.getUid())
	private final String id;
	private final String username;
	private final String password;
	private final List<GrantedAuthority> authorities;

	public CustomUserDetails(String id, String username, String password, List<GrantedAuthority> authorities) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.authorities = authorities;
	}

	// expose uid
	public String getId() {
		return id;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CustomUserDetails)) return false;
		CustomUserDetails that = (CustomUserDetails) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
