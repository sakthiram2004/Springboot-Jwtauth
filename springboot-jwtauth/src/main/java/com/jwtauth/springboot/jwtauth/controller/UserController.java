package com.jwtauth.springboot.jwtauth.controller;

import com.jwtauth.springboot.jwtauth.dto.LoginRequest;
import com.jwtauth.springboot.jwtauth.dto.LoginResponse;
import com.jwtauth.springboot.jwtauth.dto.RegisterRequest;
import com.jwtauth.springboot.jwtauth.model.User;
import com.jwtauth.springboot.jwtauth.service.JwtUtils;
import com.jwtauth.springboot.jwtauth.service.UserService;
import com.jwtauth.springboot.jwtauth.service.OurUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final OurUserDetailService userDetailService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            userService.registerUser(registerRequest.getName(), registerRequest.getEmail(), registerRequest.getPassword());
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            final UserDetails userDetails = userDetailService.loadUserByUsername(loginRequest.getEmail());
            final String jwt = jwtUtils.generateToken(userDetails);

            return ResponseEntity.ok(new LoginResponse(jwt));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }
}
