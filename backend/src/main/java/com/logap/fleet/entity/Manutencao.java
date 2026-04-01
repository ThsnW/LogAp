package com.logap.fleet.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "manutencoes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Manutencao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_finalizacao")
    private LocalDate dataFinalizacao;

    @Column(name = "tipo_servico", length = 100)
    private String tipoServico;

    @Column(name = "custo_estimado", precision = 10, scale = 2)
    private BigDecimal custoEstimado;

    @Column(length = 20)
    private String status; // PENDENTE, EM_REALIZACAO, CONCLUIDA

    @Column(length = 500)
    private String observacoes;

    @PrePersist
    protected void onCreate() {
        if (status == null) status = "PENDENTE";
    }
}
