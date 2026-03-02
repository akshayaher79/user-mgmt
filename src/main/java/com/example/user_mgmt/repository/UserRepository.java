package com.example.user_mgmt.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.user_mgmt.entity.User;


@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmailAndDeletedFalse(String email);
    Page<User> findByDeletedFalse(Pageable p);
    boolean existsByEmailAndDeletedFalse(String email);
}
