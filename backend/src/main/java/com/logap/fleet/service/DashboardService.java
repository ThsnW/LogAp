package com.logap.fleet.service;

import com.logap.fleet.dto.DashboardDTO;
import com.logap.fleet.dto.ManutencaoDTO;
import com.logap.fleet.entity.Manutencao;
import com.logap.fleet.repository.ManutencaoRepository;
import com.logap.fleet.repository.ViagemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ViagemRepository viagemRepository;
    private final ManutencaoRepository manutencaoRepository;

    public DashboardDTO getDashboardData() {
        return DashboardDTO.builder()
                .totalKmFrota(getTotalKmFrota())
                .volumePorCategoria(getVolumePorCategoria())
                .proximasManutencoes(getProximasManutencoes())
                .rankingUtilizacao(getRankingUtilizacao())
                .projecaoFinanceiraMensal(getProjecaoFinanceira())
                .build();
    }

    // Metric 1: Total KM for the fleet
    public BigDecimal getTotalKmFrota() {
        return viagemRepository.somarQuilometragemTotal();
    }

    // Metric 1b: Total KM for a specific vehicle
    public BigDecimal getTotalKmVeiculo(Long veiculoId) {
        return viagemRepository.somarQuilometragemPorVeiculo(veiculoId);
    }

    // Metric 2: Volume by category
    public List<DashboardDTO.VolumeCategoria> getVolumePorCategoria() {
        List<Object[]> results = viagemRepository.contarViagensPorCategoria();
        List<DashboardDTO.VolumeCategoria> volumes = new ArrayList<>();

        for (Object[] row : results) {
            volumes.add(DashboardDTO.VolumeCategoria.builder()
                    .categoria((String) row[0])
                    .quantidade(((Number) row[1]).longValue())
                    .build());
        }

        return volumes;
    }

    // Metric 3: Next 5 scheduled maintenances
    public List<ManutencaoDTO> getProximasManutencoes() {
        List<Manutencao> proximas = manutencaoRepository.findProximasManutencoes();
        return proximas.stream()
                .limit(5)
                .map(m -> ManutencaoDTO.builder()
                        .id(m.getId())
                        .veiculoId(m.getVeiculo().getId())
                        .veiculoPlaca(m.getVeiculo().getPlaca())
                        .veiculoModelo(m.getVeiculo().getModelo())
                        .dataInicio(m.getDataInicio())
                        .dataFinalizacao(m.getDataFinalizacao())
                        .tipoServico(m.getTipoServico())
                        .custoEstimado(m.getCustoEstimado())
                        .status(m.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    // Metric 4: Usage ranking
    public List<DashboardDTO.RankingVeiculo> getRankingUtilizacao() {
        List<Object[]> results = viagemRepository.rankingUtilizacao();
        List<DashboardDTO.RankingVeiculo> ranking = new ArrayList<>();

        for (Object[] row : results) {
            ranking.add(DashboardDTO.RankingVeiculo.builder()
                    .id(((Number) row[0]).longValue())
                    .placa((String) row[1])
                    .modelo((String) row[2])
                    .tipo((String) row[3])
                    .totalKm(row[4] instanceof BigDecimal ? (BigDecimal) row[4] : new BigDecimal(row[4].toString()))
                    .build());
        }

        return ranking;
    }

    // Metric 5: Financial projection for current month
    public BigDecimal getProjecaoFinanceira() {
        return manutencaoRepository.somarCustoEstimadoMesAtual();
    }
}
