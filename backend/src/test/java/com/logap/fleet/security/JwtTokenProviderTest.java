package com.logap.fleet.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", "mySuperSecretKeyForJWTTokenGeneration2024LogApFleetManagementSystemKey123");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", 86400000); // 1 dia
    }

    @Test
    void generateToken_DeveRetornarTokenValido() {
        String token = jwtTokenProvider.generateToken("admin@logap.com", "ADMIN");

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void getEmailFromToken_DeveExtrairEmailCorreto() {
        String token = jwtTokenProvider.generateToken("teste@logap.com", "USER");

        String emailExtraido = jwtTokenProvider.getEmailFromToken(token);
        assertEquals("teste@logap.com", emailExtraido);
    }
    
    @Test
    void validateToken_DeveRetornarFalsoParaTokenInvalido() {
        assertFalse(jwtTokenProvider.validateToken("token-completamente-invalido-random-string"));
    }
}
