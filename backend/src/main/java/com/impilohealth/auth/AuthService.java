package com.impilohealth.auth;

import com.impilohealth.auth.AuthDtos.AuthResponse;
import com.impilohealth.auth.AuthDtos.LoginRequest;
import com.impilohealth.auth.AuthDtos.RegisterRequest;
import com.impilohealth.common.exception.ConflictException;
import com.impilohealth.common.security.JwtService;
import com.impilohealth.user.User;
import com.impilohealth.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository users, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase();
        if (users.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("An account with this email already exists");
        }
        User user = users.save(new User(req.fullName().trim(), email,
                passwordEncoder.encode(req.password()), req.gender(), req.dateOfBirth()));
        return toResponse(user);
    }

    public AuthResponse login(LoginRequest req) {
        String email = req.email().trim().toLowerCase();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, req.password()));
        User user = users.findByEmailIgnoreCase(email).orElseThrow();
        return toResponse(user);
    }

    private AuthResponse toResponse(User user) {
        return new AuthResponse(jwtService.generateToken(user.getEmail(), user.getRole().name()),
                "Bearer", jwtService.getExpirationMs(), user.getFullName(), user.getEmail());
    }
}
