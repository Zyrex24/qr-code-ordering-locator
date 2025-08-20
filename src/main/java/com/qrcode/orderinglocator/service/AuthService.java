package com.qrcode.orderinglocator.service;

import com.qrcode.orderinglocator.dto.auth.AuthResponse;
import com.qrcode.orderinglocator.dto.auth.LoginRequest;
import com.qrcode.orderinglocator.dto.auth.RegisterRequest;
import com.qrcode.orderinglocator.entity.User;
import com.qrcode.orderinglocator.exception.EmailAlreadyExistsException;
import com.qrcode.orderinglocator.repository.UserRepository;
import com.qrcode.orderinglocator.security.CustomUserDetails;
import com.qrcode.orderinglocator.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.CUSTOMER) // Default role for registration
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        // Generate JWT token
        CustomUserDetails userDetails = CustomUserDetails.fromUser(savedUser);
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", savedUser.getRole().name());
        claims.put("userId", savedUser.getId());
        
        String token = jwtUtil.generateToken(userDetails, claims);

        return AuthResponse.builder()
                .token(token)
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .phone(savedUser.getPhone())
                .role(savedUser.getRole())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        log.info("User login attempt for email: {}", request.getEmail());
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getRole().name());
        claims.put("userId", userDetails.getId());
        
        String token = jwtUtil.generateToken(userDetails, claims);
        
        log.info("User logged in successfully with ID: {}", userDetails.getId());

        return AuthResponse.builder()
                .token(token)
                .id(userDetails.getId())
                .name(userDetails.getName())
                .email(userDetails.getEmail())
                .phone(userDetails.getUsername()) // Note: username is email in our case
                .role(userDetails.getRole())
                .build();
    }
}