package com.logap.fleet.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DashboardDTO {

    private BigDecimal totalKmFrota;
    private List<VolumeCategoria> volumePorCategoria;
    private List<ManutencaoDTO> proximasManutencoes;
    private List<RankingVeiculo> rankingUtilizacao;
    private BigDecimal projecaoFinanceiraMensal;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class VolumeCategoria {
        private String categoria;
        private Long quantidade;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class RankingVeiculo {
        private Long id;
        private String placa;
        private String modelo;
        private String tipo;
        private BigDecimal totalKm;
    }
}
