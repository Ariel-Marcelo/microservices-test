package com.demo.trcuentas.infrastructure.adapters.out.persistence.reporte;

import com.demo.trcuentas.domain.reporte.CuentaReporte;
import com.demo.trcuentas.domain.reporte.EstadoCuentaReporte;
import com.demo.trcuentas.domain.reporte.MovimientoReporte;
import com.demo.trcuentas.domain.reporte.ReporteConsulta;
import com.demo.trcuentas.domain.reporte.ReporteMapper;
import com.demo.trcuentas.domain.reporte.ports.out.ReporteRepositoryPort;
import com.demo.trcuentas.infrastructure.adapters.out.persistence.cliente.ClienteCuentaJpaRepository;
import com.demo.trcuentas.infrastructure.adapters.out.persistence.cuenta.CuentaJpaRepository;
import com.demo.trcuentas.infrastructure.adapters.out.persistence.models.ClienteCuenta;
import com.demo.trcuentas.infrastructure.adapters.out.persistence.models.Cuenta;
import com.demo.trcuentas.infrastructure.adapters.out.persistence.models.Movimiento;
import com.demo.trcuentas.infrastructure.adapters.out.persistence.movimiento.MovimientoJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReporteRepositoryAdapter implements ReporteRepositoryPort {

    private final ClienteCuentaJpaRepository clienteRepository;
    private final CuentaJpaRepository cuentaRepository;
    private final MovimientoJpaRepository movimientoRepository;
    private final ReporteMapper reporteMapper;

    @Override
    public Optional<EstadoCuentaReporte> obtenerEstadoCuentaCompleto(ReporteConsulta consulta) {
        String clienteId = consulta.getClienteId();
        
        Optional<ClienteCuenta> clienteOpt = clienteRepository.findByClienteId(clienteId);
        if (clienteOpt.isEmpty()) {
            return Optional.empty();
        }
        
        ClienteCuenta cliente = clienteOpt.get();

        // Search Cuentas by ClientId
        List<Cuenta> cuentas = cuentaRepository.findByCliente_ClienteId(clienteId);
        
        if (cuentas.isEmpty()) {
            return Optional.of(buildEmptyReport(cliente, consulta));
        }

        List<Long> cuentaIds = cuentas.stream().map(Cuenta::getId).collect(Collectors.toList());

        // Search Movements by IDs Cuentas
        LocalDateTime inicio = consulta.getFechaInicio().atStartOfDay();
        LocalDateTime fin = consulta.getFechaFin().atTime(LocalTime.MAX);
        List<Movimiento> allMovements = movimientoRepository.findByCuentaIdInAndFechaBetweenOrderByFechaDesc(cuentaIds, inicio, fin);

        // Build Report
        Map<Long, List<Movimiento>> movementsByCuenta = allMovements.stream()
                .collect(Collectors.groupingBy(m -> m.getCuenta().getId()));

        List<CuentaReporte> accountsDomain = cuentas.stream().map(cuenta -> {

            List<Movimiento> movements = movementsByCuenta.getOrDefault(cuenta.getId(), Collections.emptyList());

            List<MovimientoReporte> movementsDomain = movements.stream()
                    .map(reporteMapper::toMovimientoDomain)
                    .collect(Collectors.toList());

            CuentaReporte accountDomain = reporteMapper.toCuentaDomain(cuenta);
            accountDomain.setMovimientos(movementsDomain);
            return accountDomain;

        }).collect(Collectors.toList());

        return Optional.of(EstadoCuentaReporte.builder()
                .clienteId(cliente.getClienteId())
                .nombreCliente(cliente.getNombre())
                .rangoFechasSolicitado(consulta.getFechaInicio() + " a " + consulta.getFechaFin())
                .cuentas(accountsDomain)
                .build());
    }

    private EstadoCuentaReporte buildEmptyReport(ClienteCuenta cliente, ReporteConsulta consulta) {
        return EstadoCuentaReporte.builder()
                .clienteId(cliente.getClienteId())
                .nombreCliente(cliente.getNombre())
                .rangoFechasSolicitado(consulta.getFechaInicio() + " a " + consulta.getFechaFin())
                .cuentas(Collections.emptyList())
                .build();
    }
}
