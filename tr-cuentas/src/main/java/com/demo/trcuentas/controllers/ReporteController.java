package com.demo.trcuentas.controllers;

import com.demo.trcuentas.application.ReporteService;
import com.demo.trcuentas.domain.dtos.ApiResponse;
import com.demo.trcuentas.domain.dtos.reporte.response.ReporteEstadoCuentaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/{clienteId}")
    public ResponseEntity<ApiResponse<ReporteEstadoCuentaResponse>> getReport(
            @PathVariable String clienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        log.info("INICIO PETICIÓN: [GET /api/v1/reports/{}] - Solicitud de Reporte de Estado de Cuenta. Cliente ID: {}, Rango: {} a {}", clienteId, clienteId, startDate, endDate);
        if (startDate.isAfter(endDate)) {
            log.error("ERROR LÓGICO: Rango de fechas inválido. Fecha de inicio {} es posterior a la fecha fin {}.", startDate, endDate);
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha fin");
        }

        ReporteEstadoCuentaResponse reporte = reporteService.generarReporte(clienteId, startDate, endDate);

        log.info("FIN PETICIÓN: [GET /api/v1/reports/{}] - Reporte generado con éxito. Status: 200 OK.", clienteId);
        return ResponseEntity.ok(ApiResponse.success(reporte));
    }
}