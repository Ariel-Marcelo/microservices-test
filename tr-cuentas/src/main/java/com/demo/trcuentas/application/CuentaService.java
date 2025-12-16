package com.demo.trcuentas.application;

import com.demo.trcuentas.domain.dtos.cuenta.CuentaMapper;
import com.demo.trcuentas.domain.dtos.cuenta.CuentaRepositoryPort;
import com.demo.trcuentas.domain.dtos.cuenta.CuentaServicePort;
import com.demo.trcuentas.domain.dtos.cuenta.requests.CuentaRequest;
import com.demo.trcuentas.domain.dtos.cuenta.responses.CuentaResponse;
import com.demo.trcuentas.domain.dtos.movimiento.MovimientoRepositoryPort;
import com.demo.trcuentas.domain.models.ClienteCuenta;
import com.demo.trcuentas.domain.models.Cuenta;
import com.demo.trcuentas.domain.models.Movimiento;
import com.demo.trcuentas.infrastructure.repositories.cliente.ClienteCuentaJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CuentaService implements CuentaServicePort {

    private final CuentaRepositoryPort cuentaRepository;
    private final MovimientoRepositoryPort movimientoRepository;
    private final ClienteCuentaJpaRepository clienteCuentaJpaRepository;

    @Override
    public CuentaResponse create(CuentaRequest cuentaRequest) {
        log.info("INICIO CREATE CUENTA: Solicitud para crear cuenta con N° {} para Cliente ID {}.", cuentaRequest.getNumeroCuenta(), cuentaRequest.getClienteId());

        ClienteCuenta cliente = clienteCuentaJpaRepository.findByIdAndEstadoTrue(cuentaRequest.getClienteId())
                .orElseThrow(() -> {
                    log.warn("FALLO CREATE CUENTA: Cliente {} no encontrado o inactivo.", cuentaRequest.getClienteId());
                    return new EntityNotFoundException("Cliente no encontrado o inactivo con ID: " + cuentaRequest.getClienteId());
                });

        Cuenta cuenta = CuentaMapper.INSTANCE.toEntity(cuentaRequest);
        cuenta.setCliente(cliente);
        Cuenta savedCuenta = cuentaRepository.save(cuenta);

        log.info("CUENTA CREADA: Cuenta N° {} guardada con ID {}. Saldo inicial: {}", savedCuenta.getNumeroCuenta(), savedCuenta.getId(), savedCuenta.getSaldoInicial());

        if (savedCuenta.getSaldoInicial().compareTo(BigDecimal.ZERO) > 0) {
            log.info("MOVIMIENTO INICIAL: Generando movimiento de crédito inicial por {}", savedCuenta.getSaldoInicial());
            Movimiento movimientoInicial = new Movimiento();
            movimientoInicial.setFecha(java.time.LocalDateTime.now());
            movimientoInicial.setTipoMovimiento("Credito");
            movimientoInicial.setValor(savedCuenta.getSaldoInicial());
            movimientoInicial.setSaldo(savedCuenta.getSaldoInicial());
            movimientoInicial.setCuenta(savedCuenta);
            movimientoRepository.save(movimientoInicial);
            log.debug("Movimiento inicial guardado.");
        }

        log.info("FIN CREATE CUENTA: Cuenta N° {} creada y proceso completado.", savedCuenta.getNumeroCuenta());
        return CuentaMapper.INSTANCE.toResponse(savedCuenta);
    }

    @Override
    public List<CuentaResponse> getAll() {
        log.info("INICIO GET ALL CUENTAS: Recuperando todas las cuentas activas.");
        List<CuentaResponse> response = cuentaRepository.getAllActiveCuentas().stream().map(CuentaMapper.INSTANCE::toResponse).collect(Collectors.toList());
        log.info("FIN GET ALL CUENTAS: {} cuentas activas recuperadas.", response.size());
        return response;
    }

    @Override
    public CuentaResponse getById(Long id) {
        log.info("INICIO GET BY ID CUENTA: Buscando cuenta activa por ID {}.", id);
        CuentaResponse response = CuentaMapper.INSTANCE.toResponse(cuentaRepository.getActiveCuentasById(id));
        log.info("FIN GET BY ID CUENTA: Cuenta ID {} encontrada.", id);
        return response;
    }

    @Override
    public CuentaResponse update(Long id, CuentaRequest cuentaRequest) {
        log.warn("INICIO UPDATE CUENTA: Solicitud de actualización para Cuenta ID {}.", id);

        ClienteCuenta cliente = clienteCuentaJpaRepository.findByIdAndEstadoTrue(cuentaRequest.getClienteId())
                .orElseThrow(() -> {
                    log.warn("FALLO UPDATE CUENTA: Cliente {} no encontrado o inactivo.", cuentaRequest.getClienteId());
                    return new EntityNotFoundException("Cliente no encontrado o inactivo con ID: " + cuentaRequest.getClienteId());
                });

        Cuenta cuenta = cuentaRepository.getActiveCuentasById(id);
        BigDecimal pastAmount = cuenta.getSaldoInicial();
        
        log.debug("Estado inicial de Cuenta {}: Saldo previo {}, Nuevo saldo solicitado {}", cuenta.getNumeroCuenta(), pastAmount, cuentaRequest.getSaldoInicial());

        CuentaMapper.INSTANCE.updateEntityFromRequest(cuentaRequest, cuenta);
        cuenta.setCliente(cliente);
        Cuenta cuentaUpdated = cuentaRepository.save(cuenta);
        BigDecimal newAmount = cuentaUpdated.getSaldoInicial();

        if (pastAmount.compareTo(newAmount) != 0) {
            String tipoMovement = "Credito";
            if (pastAmount.compareTo(newAmount) > 0) {
                tipoMovement = "Debito";
            }
            BigDecimal diferencia = newAmount.subtract(pastAmount);
            
            log.warn("AJUSTE DE SALDO: Saldo modificado de {} a {}. Diferencia: {}. Tipo: {}", pastAmount, newAmount, diferencia, tipoMovement);

            Movimiento ajuste = new Movimiento();
            ajuste.setFecha(java.time.LocalDateTime.now());
            ajuste.setTipoMovimiento(tipoMovement);
            ajuste.setValor(diferencia);
            ajuste.setSaldo(newAmount);
            ajuste.setCuenta(cuentaUpdated);

            movimientoRepository.save(ajuste);
            log.info("MOVIMIENTO DE AJUSTE CREADO. Saldo final: {}", newAmount);
        }

        log.warn("FIN UPDATE CUENTA: Cuenta ID {} actualizada con éxito.", id);
        return CuentaMapper.INSTANCE.toResponse(cuentaUpdated);
    }

    @Override
    public void delete(Long id) {
        log.warn("INICIO DELETE CUENTA: Solicitud de inhabilitación de Cuenta ID {}.", id);
        
        Cuenta cuenta = cuentaRepository.getActiveCuentasById(id);
        cuenta.setEstado(false);
        cuentaRepository.save(cuenta);
        
        log.warn("FIN DELETE CUENTA: Cuenta N° {} marcada como inactiva (Borrado Lógico).", cuenta.getNumeroCuenta());
    }
}