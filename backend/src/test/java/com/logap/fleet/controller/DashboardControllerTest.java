package com.logap.fleet.controller;

import com.logap.fleet.dto.DashboardDTO;
import com.logap.fleet.service.DashboardService;
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
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class DashboardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DashboardService dashboardService;

    @InjectMocks
    private DashboardController dashboardController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dashboardController).build();
    }

    @Test
    void testGetDashboard() throws Exception {
        DashboardDTO dto = DashboardDTO.builder()
                .totalKmFrota(new BigDecimal("100.50"))
                .volumePorCategoria(Collections.emptyList())
                .proximasManutencoes(Collections.emptyList())
                .rankingUtilizacao(Collections.emptyList())
                .projecaoFinanceiraMensal(new BigDecimal("500.00"))
                .build();

        when(dashboardService.getDashboardData()).thenReturn(dto);

        mockMvc.perform(get("/api/dashboard")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalKmFrota").value(100.5))
                .andExpect(jsonPath("$.projecaoFinanceiraMensal").value(500.0));
    }

    @Test
    void testGetKmPorVeiculo() throws Exception {
        when(dashboardService.getTotalKmVeiculo(1L)).thenReturn(new BigDecimal("150.75"));

        mockMvc.perform(get("/api/dashboard/km/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(150.75));
    }
}
