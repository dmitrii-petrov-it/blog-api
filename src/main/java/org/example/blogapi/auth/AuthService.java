package org.example.blogapi.auth;

import lombok.RequiredArgsConstructor;
import org.example.blogapi.auth.dto.AuthResponse;
import org.example.blogapi.auth.dto.LoginRequest;
import org.example.blogapi.auth.dto.RegisterRequest;
import org.example.blogapi.user.Role;
import org.example.blogapi.user.User;
import org.example.blogapi.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authentificationManager;


    public AuthResponse register(RegisterRequest request){

        userRepository.findByUsername(request.getUsername()).ifPresent(user -> {
            throw new RuntimeException("Username already exists");
        });
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user);
        return new AuthResponse("User registered successfully");



    }

    public AuthResponse login(LoginRequest request){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword());
        authentificationManager.authenticate(authenticationToken);
        return new AuthResponse("Logged in successfully");
    }



}
