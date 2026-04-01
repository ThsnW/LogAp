package com.logap.fleet.repository;

import com.logap.fleet.entity.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
    List<Veiculo> findByTipo(String tipo);
}
