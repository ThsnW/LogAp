package com.logap.fleet.service;

import com.logap.fleet.dto.DashboardDTO;
import com.logap.fleet.dto.ManutencaoDTO;
import com.logap.fleet.entity.Manutencao;
import com.logap.fleet.entity.Veiculo;
import com.logap.fleet.repository.ManutencaoRepository;
import com.logap.fleet.repository.ViagemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DashboardServiceTest {

    @Mock
    private ViagemRepository viagemRepository;

    @Mock
    private ManutencaoRepository manutencaoRepository;

    @InjectMocks
    private DashboardService dashboardService;

    private Veiculo veiculoTest;
    private Manutencao manutencaoTest;

    @BeforeEach
    void setUp() {
        veiculoTest = Veiculo.builder()
                .id(1L)
                .placa("ABC-1234")
                .modelo("Fiorino")
                .tipo("LEVE")
                .build();

        manutencaoTest = Manutencao.builder()
                .id(1L)
                .veiculo(veiculoTest)
                .dataInicio(LocalDate.now().plusDays(1))
                .dataFinalizacao(LocalDate.now().plusDays(2))
                .tipoServico("OLEO")
                .custoEstimado(new BigDecimal("350.00"))
                .status("PENDENTE")
                .build();
    }

    @Test
    void buscarDashboardCompleto_DeveRetornarMetricasComSucesso() {
        // Mocking repo values for the dashboard
        when(viagemRepository.somarQuilometragemTotal()).thenReturn(new BigDecimal("15000.50"));
        
        List<Object[]> volumePorCategoriaMock = new ArrayList<>();
        volumePorCategoriaMock.add(new Object[]{"LEVE", 40L});
        volumePorCategoriaMock.add(new Object[]{"PESADO", 15L});
        when(viagemRepository.contarViagensPorCategoria()).thenReturn(volumePorCategoriaMock);

        List<Manutencao> manutencoesMock = List.of(manutencaoTest);
        when(manutencaoRepository.findProximasManutencoes()).thenReturn(manutencoesMock);

        List<Object[]> rankingMock = new ArrayList<>();
        rankingMock.add(new Object[]{1L, "ABC-1234", "Fiorino", "LEVE", new BigDecimal("5000.50")});
        when(viagemRepository.rankingUtilizacao()).thenReturn(rankingMock);

        when(manutencaoRepository.somarCustoEstimadoMesAtual()).thenReturn(new BigDecimal("1850.00"));

        // Act
        DashboardDTO dashboard = dashboardService.getDashboardData();

        // Assert
        assertNotNull(dashboard);
        assertEquals(new BigDecimal("15000.50"), dashboard.getTotalKmFrota());
        assertEquals(2, dashboard.getVolumePorCategoria().size());
        assertEquals(1, dashboard.getProximasManutencoes().size());
        assertEquals(1, dashboard.getRankingUtilizacao().size());
        assertEquals(new BigDecimal("1850.00"), dashboard.getProjecaoFinanceiraMensal());
    }
}
