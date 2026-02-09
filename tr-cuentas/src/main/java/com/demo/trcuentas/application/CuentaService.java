package com.demo.trcuentas.application;

import com.demo.trcuentas.domain.clienteCuenta.ClienteDomain;
import com.demo.trcuentas.domain.clienteCuenta.ClienteReplicaRepositoryPort;
import com.demo.trcuentas.domain.cuenta.CuentaDomain;
import com.demo.trcuentas.domain.cuenta.CuentaRepositoryPort;
import com.demo.trcuentas.domain.cuenta.CuentaRequestDomain;
import com.demo.trcuentas.domain.cuenta.CuentaServicePort;
import com.demo.trcuentas.domain.movimiento.MovimientoDomain;
import com.demo.trcuentas.domain.movimiento.MovimientoRepositoryPort;

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
    public CuentaDomain create(CuentaRequestDomain request) {
        log.info("INICIO CREATE CUENTA: Solicitud para crear cuenta con N° {} para Cliente ID {}.", request.getNumeroCuenta(), request.getClienteId());

        ClienteDomain cliente = clienteReplicaRepository.findByIdAndEstadoTrue(request.getClienteId())
                .orElseThrow(() -> {
                    log.warn("FALLO CREATE CUENTA: Cliente {} no encontrado o inactivo.", request.getClienteId());
                    return new EntityNotFoundException("Cliente no encontrado o inactivo con ID: " + request.getClienteId());
                });

        CuentaDomain cuenta = CuentaDomain.builder()
                .numeroCuenta(request.getNumeroCuenta())
                .tipoCuenta(request.getTipoCuenta())
                .saldoInicial(request.getSaldoInicial())
                .estado(true)
                .clienteId(cliente.getId())
                .build();

        CuentaDomain savedCuenta = cuentaRepository.save(cuenta);

        log.info("CUENTA CREADA: Cuenta N° {} guardada con ID {}. Saldo inicial: {}", savedCuenta.getNumeroCuenta(), savedCuenta.getId(), savedCuenta.getSaldoInicial());

        if (savedCuenta.getSaldoInicial().compareTo(BigDecimal.ZERO) > 0) {
            log.info("MOVIMIENTO INICIAL: Generando movimiento de crédito inicial por {}", savedCuenta.getSaldoInicial());
            MovimientoDomain movimientoInicial = MovimientoDomain.builder()
                    .fecha(LocalDateTime.now())
                    .tipoMovimiento("Credito")
                    .valor(savedCuenta.getSaldoInicial())
                    .saldo(savedCuenta.getSaldoInicial())
                    .cuentaId(savedCuenta.getId())
                    .build();
            movimientoRepository.save(movimientoInicial);
            log.debug("Movimiento inicial guardado.");
        }

        log.info("FIN CREATE CUENTA: Cuenta N° {} creada y proceso completado.", savedCuenta.getNumeroCuenta());
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
    public CuentaDomain update(Long id, CuentaRequestDomain request) {
        log.warn("INICIO UPDATE CUENTA: Solicitud de actualización para Cuenta ID {}.", id);

        ClienteDomain cliente = clienteReplicaRepository.findByIdAndEstadoTrue(request.getClienteId())
                .orElseThrow(() -> {
                    log.warn("FALLO UPDATE CUENTA: Cliente {} no encontrado o inactivo.", request.getClienteId());
                    return new EntityNotFoundException("Cliente no encontrado o inactivo con ID: " + request.getClienteId());
                });

        CuentaDomain cuenta = cuentaRepository.getActiveCuentasById(id);
        BigDecimal pastAmount = cuenta.getSaldoInicial();
        
        log.debug("Estado inicial de Cuenta {}: Saldo previo {}, Nuevo saldo solicitado {}", cuenta.getNumeroCuenta(), pastAmount, request.getSaldoInicial());

        cuenta.setNumeroCuenta(request.getNumeroCuenta());
        cuenta.setTipoCuenta(request.getTipoCuenta());
        cuenta.setSaldoInicial(request.getSaldoInicial());
        cuenta.setClienteId(cliente.getId());

        CuentaDomain cuentaUpdated = cuentaRepository.save(cuenta);
        BigDecimal newAmount = cuentaUpdated.getSaldoInicial();

        if (pastAmount.compareTo(newAmount) != 0) {
            String tipoMovement = "Credito";
            if (pastAmount.compareTo(newAmount) > 0) {
                tipoMovement = "Debito";
            }
            BigDecimal diferencia = newAmount.subtract(pastAmount);
            
            log.warn("AJUSTE DE SALDO: Saldo modificado de {} a {}. Diferencia: {}. Tipo: {}", pastAmount, newAmount, diferencia, tipoMovement);

            MovimientoDomain ajuste = MovimientoDomain.builder()
                    .fecha(LocalDateTime.now())
                    .tipoMovimiento(tipoMovement)
                    .valor(diferencia)
                    .saldo(newAmount)
                    .cuentaId(cuentaUpdated.getId())
                    .build();

            movimientoRepository.save(ajuste);
            log.info("MOVIMIENTO DE AJUSTE CREADO. Saldo final: {}", newAmount);
        }

        log.warn("FIN UPDATE CUENTA: Cuenta ID {} actualizada con éxito.", id);
        return cuentaUpdated;
    }

    @Override
    public void delete(Long id) {
        log.warn("INICIO DELETE CUENTA: Solicitud de inhabilitación de Cuenta ID {}.", id);
        
        CuentaDomain cuenta = cuentaRepository.getActiveCuentasById(id);
        cuenta.setEstado(false);
        cuentaRepository.save(cuenta);
        
        log.warn("FIN DELETE CUENTA: Cuenta N° {} marcada como inactiva (Borrado Lógico).", cuenta.getNumeroCuenta());
    }
}