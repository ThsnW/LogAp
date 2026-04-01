package com.logap.fleet.service;

import com.logap.fleet.dto.AuthDTO;
import com.logap.fleet.entity.Usuario;
import com.logap.fleet.repository.UsuarioRepository;
import com.logap.fleet.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthDTO.AuthResponse login(AuthDTO.LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciais inválidas"));

        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
            throw new RuntimeException("Credenciais inválidas");
        }

        String token = tokenProvider.generateToken(usuario.getEmail(), usuario.getRole());

        return AuthDTO.AuthResponse.builder()
                .token(token)
                .tipo("Bearer")
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .role(usuario.getRole())
                .build();
    }

    public AuthDTO.AuthResponse register(AuthDTO.RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        Usuario usuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .role("USER")
                .build();

        usuarioRepository.save(usuario);

        String token = tokenProvider.generateToken(usuario.getEmail(), usuario.getRole());

        return AuthDTO.AuthResponse.builder()
                .token(token)
                .tipo("Bearer")
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .role(usuario.getRole())
                .build();
    }
}
