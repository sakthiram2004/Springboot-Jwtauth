package com.jwtauth.springboot.jwtauth.repository;

import com.jwtauth.springboot.jwtauth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String username);
}
