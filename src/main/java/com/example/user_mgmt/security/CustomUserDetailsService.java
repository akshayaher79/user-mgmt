package com.example.user_mgmt.security;

import com.example.user_mgmt.entity.User;
import com.example.user_mgmt.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repo;

    public CustomUserDetailsService(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = repo.findByEmailAndDeletedFalse(username.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        GrantedAuthority auth = new SimpleGrantedAuthority(u.getRole());
        return new CustomUserDetails(
                String.valueOf(u.getId()),
                u.getEmail(),
                u.getPasswordHash(),
                List.of(auth)
        );
    }
}
