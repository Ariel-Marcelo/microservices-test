package com.demo.trcuentas.application;

import com.demo.trcuentas.domain.dtos.cuenta.CuentaRepositoryPort;
import com.demo.trcuentas.domain.dtos.movimiento.MovimientoRepositoryPort;
import com.demo.trcuentas.domain.dtos.reporte.ReporteMapper;
import com.demo.trcuentas.domain.dtos.reporte.response.ReporteCuenta;
import com.demo.trcuentas.domain.dtos.reporte.response.ReporteEstadoCuentaResponse;
import com.demo.trcuentas.domain.dtos.reporte.response.ReporteMovimiento;
import com.demo.trcuentas.domain.models.ClienteCuenta;
import com.demo.trcuentas.domain.models.Cuenta;
import com.demo.trcuentas.domain.models.Movimiento;
import com.demo.trcuentas.infrastructure.repositories.cliente.ClienteCuentaJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ClienteCuentaJpaRepository clienteRepository;
    private final CuentaRepositoryPort cuentaRepository;
    private final MovimientoRepositoryPort movimientoRepository;

    @Transactional(readOnly = true)
    public ReporteEstadoCuentaResponse generarReporte(String clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("INICIO REPORTE: Solicitando reporte para Cliente ID {} en rango: {} a {}", clienteId, fechaInicio, fechaFin);

        ClienteCuenta cliente = clienteRepository.findByClienteId(clienteId)
                .orElseThrow(() -> {
                    log.warn("FALLO REPORTE: Cliente no encontrado con ID: {}", clienteId);
                    return new EntityNotFoundException("Cliente no encontrado o inactivo con ID: " + clienteId);
                });
        
        log.debug("Cliente encontrado: {}", cliente.getNombre());

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);

        List<Cuenta> cuentas = cuentaRepository.getCuentasByCliente(clienteId);
        
        if (cuentas.isEmpty()) {
            log.warn("REPORTE COMPLETO: Cliente {} no tiene cuentas activas. Se devuelve reporte vacío.", clienteId);
        } else {
            log.info("REPORTE PROCESANDO: Cliente {} tiene {} cuentas a procesar.", clienteId, cuentas.size());
        }
        
        List<ReporteCuenta> accounts = new ArrayList<>();

        for (Cuenta cuenta : cuentas) {
            log.debug("Procesando movimientos para Cuenta N°: {}", cuenta.getNumeroCuenta());
            
            List<Movimiento> movements = movimientoRepository.getMovimientosByCuentaAndFechaOrderDesc(cuenta.getId(), inicio, fin);
            
            log.debug("Cuenta N° {} encontró {} movimientos en el rango solicitado.", cuenta.getNumeroCuenta(), movements.size());

            List<ReporteMovimiento> movementsReport = movements.stream()
                    .map(ReporteMapper.INSTANCE::toMovimientoDto)
                    .collect(Collectors.toList());

            ReporteCuenta accountReport = ReporteMapper.INSTANCE.toCuentaDto(cuenta);
            accountReport.setMovimientos(movementsReport);

            accounts.add(accountReport);
        }

        ReporteEstadoCuentaResponse report = new ReporteEstadoCuentaResponse();
        report.setClienteId(cliente.getClienteId());
        report.setNombreCliente(cliente.getNombre());
        report.setRangoFechasSolicitado(fechaInicio + " a " + fechaFin);
        report.setCuentas(accounts);

        log.info("FIN REPORTE: Reporte generado exitosamente para Cliente ID {}. Total de {} cuentas procesadas.", clienteId, accounts.size());
        return report;
    }
}