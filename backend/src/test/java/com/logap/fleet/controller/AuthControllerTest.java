package com.logap.fleet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logap.fleet.dto.AuthDTO;
import com.logap.fleet.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void testLoginSucesso() throws Exception {
        AuthDTO.LoginRequest req = new AuthDTO.LoginRequest();
        req.setEmail("test@logap.com");
        req.setSenha("123456");

        AuthDTO.AuthResponse res = AuthDTO.AuthResponse.builder()
                .token("mockToken")
                .tipo("Bearer")
                .nome("Admin Test")
                .email("test@logap.com")
                .build();

        when(authService.login(any(AuthDTO.LoginRequest.class))).thenReturn(res);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mockToken"));
    }

    @Test
    void testLoginFalha() throws Exception {
        AuthDTO.LoginRequest req = new AuthDTO.LoginRequest();
        req.setEmail("test@logap.com");
        req.setSenha("wrongpass");

        when(authService.login(any(AuthDTO.LoginRequest.class)))
                .thenThrow(new RuntimeException("Credenciais inválidas"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Credenciais inválidas"));
    }
}
