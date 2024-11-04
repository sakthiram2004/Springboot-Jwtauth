package com.jwtauth.springboot.jwtauth.service;

import com.jwtauth.springboot.jwtauth.model.User;
import com.jwtauth.springboot.jwtauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(String name, String email, String password) {
        if (userRepository.findByEmail(email) != null) {
            throw new RuntimeException("User with email " + email + " already exists");
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }
}