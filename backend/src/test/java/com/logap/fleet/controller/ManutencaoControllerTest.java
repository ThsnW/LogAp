package com.logap.fleet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logap.fleet.dto.ManutencaoDTO;
import com.logap.fleet.service.ManutencaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ManutencaoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ManutencaoService manutencaoService;

    @InjectMocks
    private ManutencaoController manutencaoController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(manutencaoController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Para suporte ao LocalDate
    }

    @Test
    void testListarTodas() throws Exception {
        ManutencaoDTO dto = ManutencaoDTO.builder()
                .id(1L)
                .tipoServico("OLEO")
                .build();

        when(manutencaoService.listarTodas()).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/api/manutencoes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoServico").value("OLEO"));
    }

    @Test
    void testCriarManutencao() throws Exception {
        ManutencaoDTO mockReq = ManutencaoDTO.builder()
                .veiculoId(1L)
                .dataInicio(LocalDate.now().plusDays(2))
                .tipoServico("FREIOS")
                .custoEstimado(new BigDecimal("150.00"))
                .status("PENDENTE")
                .build();

        ManutencaoDTO mockRes = ManutencaoDTO.builder().id(10L).tipoServico("FREIOS").build();

        when(manutencaoService.criar(any(ManutencaoDTO.class))).thenReturn(mockRes);

        mockMvc.perform(post("/api/manutencoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L));
    }
}
