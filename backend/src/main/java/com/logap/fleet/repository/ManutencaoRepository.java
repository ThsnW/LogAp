package com.logap.fleet.repository;

import com.logap.fleet.entity.Manutencao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ManutencaoRepository extends JpaRepository<Manutencao, Long> {

    List<Manutencao> findByVeiculoId(Long veiculoId);

    List<Manutencao> findByStatus(String status);

    // Dashboard: Next 5 scheduled (or delayed) maintenances ordered by date
    @Query("SELECT m FROM Manutencao m WHERE m.status <> 'CONCLUIDA' ORDER BY m.dataInicio ASC")
    List<Manutencao> findProximasManutencoes();

    // Dashboard: Total estimated cost for current month
    @Query(value = "SELECT COALESCE(SUM(custo_estimado), 0) FROM manutencoes " +
            "WHERE EXTRACT(MONTH FROM data_inicio) = EXTRACT(MONTH FROM CURRENT_DATE) " +
            "AND EXTRACT(YEAR FROM data_inicio) = EXTRACT(YEAR FROM CURRENT_DATE)", nativeQuery = true)
    BigDecimal somarCustoEstimadoMesAtual();

    @Query("SELECT m FROM Manutencao m " +
           "WHERE (:placa IS NULL OR m.veiculo.placa = :placa) " +
           "AND (:modelo IS NULL OR m.veiculo.modelo = :modelo) " +
           "AND (:tipo IS NULL OR m.veiculo.tipo = :tipo) " +
           "AND (CAST(:dataInicio AS date) IS NULL OR m.dataInicio >= :dataInicio) " +
           "AND (CAST(:dataFim AS date) IS NULL OR m.dataInicio <= :dataFim) " +
           "ORDER BY m.dataInicio DESC")
    List<Manutencao> findWithFilters(@Param("placa") String placa,
                                     @Param("modelo") String modelo,
                                     @Param("tipo") String tipo,
                                     @Param("dataInicio") LocalDate dataInicio, 
                                     @Param("dataFim") LocalDate dataFim);
}
