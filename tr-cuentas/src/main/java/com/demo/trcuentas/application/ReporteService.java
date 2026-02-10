package com.demo.trcuentas.application;

import com.demo.trcuentas.domain.reporte.ReporteRepositoryPort;
import com.demo.trcuentas.domain.reporte.ReporteConsulta;
import com.demo.trcuentas.domain.reporte.EstadoCuentaReporte;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ReporteRepositoryPort reporteRepository;

    @Transactional(readOnly = true)
    public EstadoCuentaReporte generarReporte(ReporteConsulta consulta) {
        log.info("INICIO REPORTE: Solicitando reporte para Cliente ID {} en rango: {} a {}", 
                consulta.getClienteId(), consulta.getFechaInicio(), consulta.getFechaFin());

        return reporteRepository.obtenerEstadoCuentaCompleto(consulta)
                .orElseThrow(() -> {
                    log.warn("FALLO REPORTE: Cliente no encontrado con ID: {}", consulta.getClienteId());
                    return new EntityNotFoundException("Cliente no encontrado o inactivo con ID: " + consulta.getClienteId());
                });
    }
}
