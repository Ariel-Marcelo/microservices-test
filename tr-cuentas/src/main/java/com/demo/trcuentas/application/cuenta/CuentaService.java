package com.demo.trcuentas.application.cuenta;

import com.demo.trcuentas.domain.clienteCuenta.ports.out.ClienteReplicaRepositoryPort;
import com.demo.trcuentas.domain.cuenta.CuentaDomain;
import com.demo.trcuentas.domain.cuenta.ports.out.CuentaRepositoryPort;
import com.demo.trcuentas.domain.cuenta.ports.in.CuentaServicePort;
import com.demo.trcuentas.domain.movimiento.MovimientoDomain;
import com.demo.trcuentas.infrastructure.adapters.in.rest.dtos.TipoMovimiento;
import com.demo.trcuentas.domain.movimiento.ports.out.MovimientoRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CuentaService implements CuentaServicePort {

    private final CuentaRepositoryPort cuentaRepository;
    private final MovimientoRepositoryPort movimientoRepository;
    private final ClienteReplicaRepositoryPort clienteReplicaRepository;

    @Override
    public CuentaDomain create(CuentaDomain domain) {
        log.info("INICIO CREATE CUENTA: Solicitud para cuenta N° {} de Cliente ID {}.", domain.getNumeroCuenta(), domain.getClienteId());

        clienteReplicaRepository.findByIdAndEstadoTrue(domain.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado o inactivo con ID: " + domain.getClienteId()));

        domain.setEstado(true);
        CuentaDomain savedCuenta = cuentaRepository.save(domain);

        if (savedCuenta.getSaldoInicial().compareTo(BigDecimal.ZERO) > 0) {
            log.info("MOVIMIENTO INICIAL: Generando crédito inicial por {}", savedCuenta.getSaldoInicial());
            MovimientoDomain movimientoInicial = MovimientoDomain.builder()
                    .fecha(LocalDateTime.now())
                    .tipoMovimiento(TipoMovimiento.CREDITO)
                    .valor(savedCuenta.getSaldoInicial())
                    .saldo(savedCuenta.getSaldoInicial())
                    .cuentaId(savedCuenta.getId())
                    .build();
            movimientoRepository.save(movimientoInicial);
        }

        return savedCuenta;
    }

    @Override
    public List<CuentaDomain> getAll() {
        return cuentaRepository.getAllActiveCuentas();
    }

    @Override
    public CuentaDomain getById(Long id) {
        return cuentaRepository.getActiveCuentasById(id);
    }

    @Override
    public CuentaDomain update(Long id, CuentaDomain domain) {
        log.warn("INICIO UPDATE CUENTA: Solicitud para ID {}.", id);

        clienteReplicaRepository.findByIdAndEstadoTrue(domain.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado o inactivo con ID: " + domain.getClienteId()));

        CuentaDomain cuentaExistente = cuentaRepository.getActiveCuentasById(id);
        domain.setId(id);
        domain.setSaldoInicial(cuentaExistente.getSaldoInicial());
        domain.setClienteId(cuentaExistente.getClienteId());
        domain.setEstado(cuentaExistente.getEstado());

        return cuentaRepository.save(domain);
    }

    @Override
    public void delete(Long id) {
        CuentaDomain cuenta = cuentaRepository.getActiveCuentasById(id);
        cuenta.setEstado(false);
        cuentaRepository.save(cuenta);
    }
}
