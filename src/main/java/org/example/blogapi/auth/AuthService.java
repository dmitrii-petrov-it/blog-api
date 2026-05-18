package org.example.blogapi.auth;

import lombok.RequiredArgsConstructor;
import org.example.blogapi.auth.dto.AuthResponse;
import org.example.blogapi.auth.dto.LoginRequest;
import org.example.blogapi.auth.dto.RegisterRequest;
import org.example.blogapi.exception.ResourceAlreadyExistsException;
import org.example.blogapi.security.JwtService;
import org.example.blogapi.user.CustomUserDetailsService;
import org.example.blogapi.user.Role;
import org.example.blogapi.user.User;
import org.example.blogapi.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public AuthResponse register(RegisterRequest request) {
        userRepository.findByUsername(request.getUsername()).ifPresent(user -> {
            throw new ResourceAlreadyExistsException("Username already exists");
        });

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse("User registered successfully", token);
    }

    public AuthResponse login(LoginRequest request) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());

        Authentication authentication = authenticationManager.authenticate(authToken);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails);

        return new AuthResponse("Logged in successfully", token);
    }
}