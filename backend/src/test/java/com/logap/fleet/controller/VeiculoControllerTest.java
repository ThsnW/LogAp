package com.logap.fleet.controller;

import com.logap.fleet.entity.Veiculo;
import com.logap.fleet.repository.VeiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class VeiculoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VeiculoRepository veiculoRepository;

    @InjectMocks
    private VeiculoController veiculoController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(veiculoController).build();
    }

    @Test
    void testListarTodos() throws Exception {
        Veiculo veiculo = Veiculo.builder()
                .id(1L)
                .placa("ABC-1234")
                .build();

        when(veiculoRepository.findAll()).thenReturn(Collections.singletonList(veiculo));

        mockMvc.perform(get("/api/veiculos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].placa").value("ABC-1234"));
    }

    @Test
    void testBuscarPorIdSucesso() throws Exception {
        Veiculo veiculo = Veiculo.builder()
                .id(1L)
                .placa("XYZ-9999")
                .build();

        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));

        mockMvc.perform(get("/api/veiculos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value("XYZ-9999"));
    }

    @Test
    void testBuscarPorIdNaoEncontrado() throws Exception {
        when(veiculoRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/veiculos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
