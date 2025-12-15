package com.demo.trcuentas.controllers;

import com.demo.trcuentas.application.ReporteService;
import com.demo.trcuentas.domain.dtos.reporte.response.ReporteEstadoCuentaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/{clienteId}")
    public ResponseEntity<ReporteEstadoCuentaResponse> getReport(
            @PathVariable String clienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha fin");
        }

        return ResponseEntity.ok(reporteService.generarReporte(clienteId, startDate, endDate));
    }
}
