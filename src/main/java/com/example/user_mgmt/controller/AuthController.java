package com.example.user_mgmt.controller;

import com.example.user_mgmt.dto.LoginRequest;
import com.example.user_mgmt.dto.SignupRequest;
import com.example.user_mgmt.dto.UserResponse;
import com.example.user_mgmt.entity.User;
import com.example.user_mgmt.security.JwtService;
import com.example.user_mgmt.service.UserService;
import com.example.user_mgmt.security.CustomUserDetails;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(AuthenticationManager authManager, JwtService jwtService, UserService userService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignupRequest req) {
        User u = userService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(u));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest req) {
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        CustomUserDetails ud = (CustomUserDetails) auth.getPrincipal();
        // include uid and roles as claim
        Map<String, Object> claims = Map.of(
                "uid", ud.getId(),
                "roles", ud.getAuthorities().stream().map(Object::toString).toList()
        );
        String token = jwtService.generateToken(ud.getUsername(), claims);
        return ResponseEntity.ok(Map.of("token", token));
    }
}
