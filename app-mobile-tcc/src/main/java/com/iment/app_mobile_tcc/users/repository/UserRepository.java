package com.iment.app_mobile_tcc.users.repository;

import com.iment.app_mobile_tcc.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
