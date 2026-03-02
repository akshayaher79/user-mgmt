package com.example.user_mgmt.service;

import com.example.user_mgmt.dto.SignupRequest;
import com.example.user_mgmt.dto.UpdateUserRequest;
import com.example.user_mgmt.entity.User;
import com.example.user_mgmt.repository.UserRepository;
import com.example.user_mgmt.util.PasswordValidator;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;


@Service
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Transactional
    public User register(SignupRequest req) {
        String email = req.getEmail().toLowerCase().trim();
        if (repo.existsByEmailAndDeletedFalse(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (!PasswordValidator.isValid(req.getPassword())) {
            throw new IllegalArgumentException("Password must be at least 8 characters and contain a number or special character");
        }
        User u = new User();
        u.setFirstName(req.getFirstName().trim());
        u.setLastName(req.getLastName().trim());
        u.setEmail(email);
        u.setPasswordHash(encoder.encode(req.getPassword()));
        u.setRole("ROLE_USER");
        return repo.save(u);
    }

    public User findById(Long id) {
        return repo.findById(id)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    public User findByEmail(String email) {
        return repo.findByEmailAndDeletedFalse(email.toLowerCase())
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    public Page<User> list(int page, int size) {
        return repo.findByDeletedFalse(PageRequest.of(page, size));
    }

    @Transactional
    public User update(Long id, UpdateUserRequest req) {
        User u = findById(id);
        String newEmail = req.getEmail().toLowerCase().trim();
        if (!u.getEmail().equalsIgnoreCase(newEmail) && repo.existsByEmailAndDeletedFalse(newEmail)) {
            throw new IllegalArgumentException("Email already in use");
        }
        u.setFirstName(req.getFirstName().trim());
        u.setLastName(req.getLastName().trim());
        u.setEmail(newEmail);
        return repo.save(u);
    }

    @Transactional
    public void softDelete(Long id) {
        User u = findById(id);
        u.setDeleted(true);
        repo.save(u);
    }
}
