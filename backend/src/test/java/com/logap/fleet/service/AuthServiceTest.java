package com.logap.fleet.service;

import com.logap.fleet.dto.AuthDTO;
import com.logap.fleet.entity.Usuario;
import com.logap.fleet.repository.UsuarioRepository;
import com.logap.fleet.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    private Usuario usuarioTest;
    private AuthDTO.LoginRequest loginRequest;
    private AuthDTO.RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        usuarioTest = Usuario.builder()
                .id(1L)
                .nome("Admin")
                .email("admin@test.com")
                .senha("encodedPassword")
                .role("ADMIN")
                .build();

        loginRequest = new AuthDTO.LoginRequest();
        loginRequest.setEmail("admin@test.com");
        loginRequest.setSenha("123456");

        registerRequest = new AuthDTO.RegisterRequest();
        registerRequest.setNome("New User");
        registerRequest.setEmail("new@test.com");
        registerRequest.setSenha("123456");
    }

    @Test
    void login_Sucesso() {
        // Arrange
        when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(usuarioTest));
        when(passwordEncoder.matches("123456", "encodedPassword")).thenReturn(true);
        when(tokenProvider.generateToken("admin@test.com", "ADMIN")).thenReturn("mocked-jwt-token");

        // Act
        AuthDTO.AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        assertEquals("Bearer", response.getTipo());
        assertEquals("Admin", response.getNome());
        assertEquals("admin@test.com", response.getEmail());
    }

    @Test
    void login_FalhaSenhaIncorreta() {
        // Arrange
        when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(usuarioTest));
        when(passwordEncoder.matches("123456", "encodedPassword")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
        assertEquals("Credenciais inválidas", exception.getMessage());
        verify(tokenProvider, never()).generateToken(anyString(), anyString());
    }

    @Test
    void register_Sucesso() {
        // Arrange
        when(usuarioRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("encodedNewPassword");
        
        // Simular o save (retornando o mesmo objeto só que salvo)
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> {
            Usuario u = i.getArgument(0);
            u.setId(2L);
            return u;
        });
        
        when(tokenProvider.generateToken("new@test.com", "USER")).thenReturn("register-jwt-token");

        // Act
        AuthDTO.AuthResponse response = authService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals("register-jwt-token", response.getToken());
        assertEquals("New User", response.getNome());
        assertEquals("USER", response.getRole());
        
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }
}
