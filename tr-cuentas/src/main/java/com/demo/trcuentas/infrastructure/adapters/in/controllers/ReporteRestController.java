package com.demo.trcuentas.infrastructure.adapters.in.controllers;

import com.demo.trcuentas.application.reporte.ReporteService;
import com.demo.trcuentas.infrastructure.adapters.in.rest.api.ReportsApi;
import com.demo.trcuentas.infrastructure.adapters.in.rest.dtos.ApiResponseReporteEstadoCuentaResponse;
import com.demo.trcuentas.infrastructure.adapters.in.mappers.RestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReporteRestController implements ReportsApi {

    private final ReporteService reporteService;
    private final RestMapper restMapper;

    @Override
    public ResponseEntity<ApiResponseReporteEstadoCuentaResponse> getReport(String clienteId, LocalDate startDate, LocalDate endDate) {
        log.info("INICIO PETICIÓN (OpenAPI): [GET /api/v1/reports/{}]", clienteId);
        
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha fin");
        }

        var consulta = restMapper.toDomain(clienteId, startDate, endDate);

        var domainResponse = reporteService.generarReporte(consulta);

        var restResponse = restMapper.toRest(domainResponse);

        ApiResponseReporteEstadoCuentaResponse response = new ApiResponseReporteEstadoCuentaResponse();
        response.setStatus(true);
        response.setData(restResponse);

        return ResponseEntity.ok(response);
    }
}
