package com.ghady.financetracker.auth;

import com.ghady.financetracker.exception.BadRequestException;
import com.ghady.financetracker.user.User;
import com.ghady.financetracker.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private AuthRequest registerRequest;
    private AuthRequest loginRequest;

    @BeforeEach
    void setUp(){
        registerRequest = new AuthRequest("Ghady", "ghady@example.com", "password123");
        loginRequest = new AuthRequest(null, "ghady@example.com","password123");
    }

    @Test
    void register_shouldReturnToken_WhenEmailIsNew(){
        when(userRepository.existsByEmail("ghady@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedpassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(jwtService.generateToken(any())).thenReturn("fake-jwt-token");

        AuthResponse response = authService.register(registerRequest);

        assertThat(response.token()).isEqualTo("fake-jwt-token");

        verify(userRepository, times(1)).existsByEmail("ghady@example.com");
        verify(userRepository, times(1)).save(any(User.class));

    }

    @Test
    void register_shouldThrowBadRequest_whenEmailAlreadyExists(){
        when(userRepository.existsByEmail("ghady@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Email already in use");

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {
        User user = User.builder()
                .email("ghady@example.com")
                .passwordHash("hashedpassword")
                .name("Ghady")
                .build();

        when(userRepository.findByEmail("ghady@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashedpassword")).thenReturn(true);
        when(jwtService.generateToken(any())).thenReturn("fake-jwt-token");

        AuthResponse response = authService.login(loginRequest);

        assertThat(response.token()).isEqualTo("fake-jwt-token");
    }

    @Test
    void login_shouldThrowBadRequest_whenEmailNotFound() {
        when(userRepository.findByEmail("ghady@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    void login_shouldThrowBadRequest_whenPasswordIsWrong() {
        User user = User.builder()
                .email("ghady@example.com")
                .passwordHash("hashedpassword")
                .build();

        when(userRepository.findByEmail("ghady@example.com")).thenReturn(Optional.of(user));
        // password doesn't match
        when(passwordEncoder.matches("password123", "hashedpassword")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Invalid email or password");
    }




}
