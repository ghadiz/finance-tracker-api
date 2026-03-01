package com.ghady.financetracker.auth;

import com.ghady.financetracker.user.User;
import com.ghady.financetracker.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(AuthRequest request){
        if(userRepository.existsByEmail(request.email())){
            throw new RuntimeException("Email already in use");
        }

        User user = User.builder()
                        .name(request.name())
                        .email(request.email())
                        .passwordHash(passwordEncoder.encode(request.password()))
                        .build();

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);

        return new AuthResponse(token);
    }
    public AuthResponse login(AuthRequest request) {

        // Load the user by email
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));


        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Credentials are valid — generate and return the token
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
}
