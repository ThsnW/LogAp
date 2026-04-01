package com.logap.fleet.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ManutencaoDTO {

    private Long id;

    @NotNull(message = "Veículo é obrigatório")
    private Long veiculoId;

    private String veiculoPlaca;
    private String veiculoModelo;

    @NotNull(message = "Data de início é obrigatória")
    private LocalDate dataInicio;

    private LocalDate dataFinalizacao;

    @NotBlank(message = "Tipo de serviço é obrigatório")
    private String tipoServico;

    @NotNull(message = "Custo estimado é obrigatório")
    @DecimalMin(value = "0.01", message = "Custo deve ser maior que zero")
    private BigDecimal custoEstimado;

    @NotBlank(message = "Status é obrigatório")
    private String status;

    @Size(max = 500, message = "Observações não podem ter mais de 500 caracteres")
    private String observacoes;
}
