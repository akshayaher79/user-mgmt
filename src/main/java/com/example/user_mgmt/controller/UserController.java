package com.example.user_mgmt.controller;

import com.example.user_mgmt.dto.UpdateUserRequest;
import com.example.user_mgmt.dto.UserResponse;
import com.example.user_mgmt.entity.User;
import com.example.user_mgmt.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService svc;

    public UserController(UserService svc) {
        this.svc = svc;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        UserDetails ud = (UserDetails) authentication.getPrincipal();
        User u = svc.findByEmail(ud.getUsername());
        return ResponseEntity.ok(UserResponse.from(u));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        User u = svc.findById(id);
        return ResponseEntity.ok(UserResponse.from(u));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<UserResponse>> list(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        Page<UserResponse> p = svc.list(page, size).map(UserResponse::from);
        return ResponseEntity.ok(p);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody UpdateUserRequest req,
                                               Authentication authentication) {
        // allow admin or self
        UserDetails ud = (UserDetails) authentication.getPrincipal();
        boolean isAdmin = ud.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        // if not admin, ensure updating own resource
        if (!isAdmin) {
            // load current user's id via email
            User current = svc.findByEmail(ud.getUsername());
            if (!current.getId().equals(id)) {
                return ResponseEntity.status(403).build();
            }
        }
        User updated = svc.update(id, req);
        return ResponseEntity.ok(UserResponse.from(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        svc.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}
