package com.logap.fleet.controller;

import com.logap.fleet.dto.DashboardDTO;
import com.logap.fleet.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardDTO> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboardData());
    }

    @GetMapping("/km/{veiculoId}")
    public ResponseEntity<BigDecimal> getKmPorVeiculo(@PathVariable Long veiculoId) {
        return ResponseEntity.ok(dashboardService.getTotalKmVeiculo(veiculoId));
    }
}
