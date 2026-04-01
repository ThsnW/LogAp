package com.logap.fleet.repository;

import com.logap.fleet.entity.Viagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface ViagemRepository extends JpaRepository<Viagem, Long> {

    // Dashboard: Total KM for entire fleet
    @Query(value = "SELECT COALESCE(SUM(km_percorrida), 0) FROM viagens", nativeQuery = true)
    BigDecimal somarQuilometragemTotal();

    // Dashboard: Total KM for a specific vehicle
    @Query(value = "SELECT COALESCE(SUM(km_percorrida), 0) FROM viagens WHERE veiculo_id = :veiculoId", nativeQuery = true)
    BigDecimal somarQuilometragemPorVeiculo(@Param("veiculoId") Long veiculoId);

    // Dashboard: Volume by category (Leve vs Pesado)
    @Query(value = "SELECT v.tipo as categoria, COUNT(vi.id) AS total " +
            "FROM viagens vi " +
            "JOIN veiculos v ON vi.veiculo_id = v.id " +
            "GROUP BY v.tipo", nativeQuery = true)
    List<Object[]> contarViagensPorCategoria();

    // Dashboard: Usage ranking - vehicles ordered by total km
    @Query(value = "SELECT v.id, v.placa, v.modelo, v.tipo as marca, COALESCE(SUM(vi.km_percorrida), 0) AS total_km " +
            "FROM veiculos v " +
            "LEFT JOIN viagens vi ON v.id = vi.veiculo_id " +
            "GROUP BY v.id, v.placa, v.modelo, v.tipo " +
            "ORDER BY total_km DESC", nativeQuery = true)
    List<Object[]> rankingUtilizacao();
}
