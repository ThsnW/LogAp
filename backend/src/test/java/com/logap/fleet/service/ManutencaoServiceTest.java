package com.logap.fleet.service;

import com.logap.fleet.dto.ManutencaoDTO;
import com.logap.fleet.entity.Manutencao;
import com.logap.fleet.entity.Veiculo;
import com.logap.fleet.repository.ManutencaoRepository;
import com.logap.fleet.repository.VeiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ManutencaoServiceTest {

    @Mock
    private ManutencaoRepository manutencaoRepository;

    @Mock
    private VeiculoRepository veiculoRepository;

    @InjectMocks
    private ManutencaoService manutencaoService;

    private Veiculo veiculoTest;
    private ManutencaoDTO manutencaoDtoTest;

    @BeforeEach
    void setUp() {
        veiculoTest = Veiculo.builder()
                .id(1L)
                .placa("ABC-1234")
                .modelo("Fiorino")
                .tipo("LEVE")
                .ano(2022)
                .build();

        manutencaoDtoTest = ManutencaoDTO.builder()
                .veiculoId(1L)
                .dataInicio(LocalDate.now().plusDays(1))
                .dataFinalizacao(LocalDate.now().plusDays(5))
                .tipoServico("TROCA_PNEUS")
                .custoEstimado(new BigDecimal("1500.00"))
                .status("PENDENTE")
                .build();
    }

    @Test
    void criacaoDeManutencaoUnidadeComSucesso() {
        // Arrange
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculoTest));
        when(manutencaoRepository.save(any(Manutencao.class))).thenAnswer(invocation -> {
            Manutencao m = invocation.getArgument(0);
            m.setId(10L);
            return m;
        });

        // Act
        ManutencaoDTO resposta = manutencaoService.criar(manutencaoDtoTest);

        // Assert
        assertNotNull(resposta);
        assertEquals(10L, resposta.getId());
        assertEquals("ABC-1234", resposta.getVeiculoPlaca());
        assertEquals("Fiorino", resposta.getVeiculoModelo());
        assertEquals("TROCA_PNEUS", resposta.getTipoServico());
        verify(veiculoRepository, times(1)).findById(1L);
        verify(manutencaoRepository, times(1)).save(any(Manutencao.class));
    }

    @Test
    void falhaDeValidacao_DataDeFinalizacaoInvalida() {
        // Arrange
        manutencaoDtoTest.setDataFinalizacao(LocalDate.now().minusDays(1)); // Finalização antes do início
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculoTest));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            manutencaoService.criar(manutencaoDtoTest);
        });

        assertEquals("Data de finalização não pode ser anterior à data de início", exception.getMessage());
        verify(manutencaoRepository, never()).save(any(Manutencao.class));
    }

    @Test
    void falhaNoVeiculoInexistente() {
        // Arrange
        when(veiculoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            manutencaoService.criar(manutencaoDtoTest);
        });

        assertEquals("Veículo não encontrado com ID: 1", exception.getMessage());
        verify(manutencaoRepository, never()).save(any(Manutencao.class));
    }
}
