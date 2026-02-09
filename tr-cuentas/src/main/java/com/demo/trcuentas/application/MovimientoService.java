package com.demo.trcuentas.application;

import com.demo.trcuentas.domain.cuenta.CuentaDomain;
import com.demo.trcuentas.domain.cuenta.CuentaRepositoryPort;
import com.demo.trcuentas.domain.movimiento.MovimientoDomain;
import com.demo.trcuentas.domain.movimiento.MovimientoRepositoryPort;
import com.demo.trcuentas.domain.movimiento.MovimientoRequestDomain;
import com.demo.trcuentas.domain.movimiento.MovimientoServicePort;
import com.demo.trcuentas.infrastructure.adapters.in.exceptions.LowBalanceException;
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
public class MovimientoService implements MovimientoServicePort {

    private final CuentaRepositoryPort cuentaRepository;
    private final MovimientoRepositoryPort movimientoRepository;

    @Override
    public MovimientoDomain create(MovimientoRequestDomain request) {
        log.info("INICIO TX CREATE: Procesando {} de {} en cuenta {}.", request.getTipoMovimiento(), request.getValor(), request.getNumeroCuenta());

        CuentaDomain cuenta = cuentaRepository
                .findActiveCuentasByNumeroId(request.getNumeroCuenta());
        
        BigDecimal movementValue = request.getValor();
        BigDecimal currentAmount = cuenta.getSaldoInicial();
        BigDecimal lastAmount;

        if ("Debito".equalsIgnoreCase(request.getTipoMovimiento())) {
            movementValue = movementValue.negate();
            lastAmount = currentAmount.add(movementValue);

            if (lastAmount.compareTo(BigDecimal.ZERO) < 0) {
                log.warn("FALLO CREATE: Saldo insuficiente. Intento de débito resultaría en saldo negativo: {}", lastAmount);
                throw new LowBalanceException("Saldo no disponible");
            }
        } else {
            lastAmount = currentAmount.add(movementValue);
        }

        MovimientoDomain movimiento = MovimientoDomain.builder()
                .fecha(LocalDateTime.now())
                .tipoMovimiento(request.getTipoMovimiento())
                .valor(movementValue)
                .saldo(lastAmount)
                .cuentaId(cuenta.getId())
                .build();

        cuenta.setSaldoInicial(lastAmount);
        cuentaRepository.save(cuenta);

        return movimientoRepository.save(movimiento);
    }

    @Override
    public List<MovimientoDomain> getAll() {
        return movimientoRepository.getAllMovimientos();
    }

    @Override
    public MovimientoDomain getById(Long id) {
        return movimientoRepository.getMovimientosById(id);
    }

    @Override
    public void delete(Long id) {
        log.warn("INICIO TX REVERSO: Solicitud de reversión para Movimiento ID: {}", id);
        
        MovimientoDomain originalMovement = movimientoRepository.getMovimientosById(id);
        CuentaDomain cuenta = cuentaRepository.getActiveCuentasById(originalMovement.getCuentaId());
        
        String nuevoTipo;
        if ("Debito".equalsIgnoreCase(originalMovement.getTipoMovimiento())) {
            nuevoTipo = "Credito";
        } else if ("Credito".equalsIgnoreCase(originalMovement.getTipoMovimiento())) {
            nuevoTipo = "Debito";
        } else {
            throw new IllegalArgumentException("No puede reversar una transacción que ya ha sido reversada");
        }

        BigDecimal balanceValue = originalMovement.getValor().negate();
        BigDecimal oldMovementValue = cuenta.getSaldoInicial().add(balanceValue);
        
        if (oldMovementValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new LowBalanceException("Saldo insuficiente");
        }

        MovimientoDomain balanceMovement = MovimientoDomain.builder()
                .fecha(LocalDateTime.now())
                .tipoMovimiento(nuevoTipo)
                .valor(balanceValue)
                .saldo(oldMovementValue)
                .cuentaId(cuenta.getId())
                .build();

        cuenta.setSaldoInicial(oldMovementValue);
        cuentaRepository.save(cuenta);
        originalMovement.setTipoMovimiento("Reversado");
        movimientoRepository.save(originalMovement);
        movimientoRepository.save(balanceMovement);
    }

    @Override
    public MovimientoDomain update(Long id, MovimientoRequestDomain request) {
        log.warn("INICIO TX UPDATE: Solicitud de actualización para Movimiento ID: {} con nuevo valor {}", id, request.getValor());
        
        MovimientoDomain originalMovement = movimientoRepository.getMovimientosById(id);
        Long cuentaId = originalMovement.getCuentaId();
        
        MovimientoDomain ultimoMovimiento = movimientoRepository.findLastByCuentaId(cuentaId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontraron movimientos para validar."));

        if (!originalMovement.getId().equals(ultimoMovimiento.getId())) {
            throw new IllegalArgumentException("Solo se permite editar el último movimiento de la cuenta para mantener la consistencia del saldo histórico.");
        }
        
        if ("Reversado".equalsIgnoreCase(originalMovement.getTipoMovimiento())) {
            throw new IllegalArgumentException("No se puede editar un movimiento que ya ha sido reversada.");
        }

        if (!originalMovement.getTipoMovimiento().equalsIgnoreCase(request.getTipoMovimiento())) {
            throw new IllegalArgumentException("No se permite cambiar el tipo de movimiento. Solo se puede ajustar el valor.");
        }

        CuentaDomain cuenta = cuentaRepository.getActiveCuentasById(cuentaId);
        
        BigDecimal saldoSinMovimientoPrevio = cuenta.getSaldoInicial().subtract(originalMovement.getValor());

        BigDecimal nuevoValor = request.getValor();
        if ("Debito".equalsIgnoreCase(request.getTipoMovimiento())) {
            nuevoValor = nuevoValor.negate();
        }

        BigDecimal nuevoSaldoFinal = saldoSinMovimientoPrevio.add(nuevoValor);

        if (nuevoSaldoFinal.compareTo(BigDecimal.ZERO) < 0) {
            throw new LowBalanceException("Saldo insuficiente para realizar esta actualización.");
        }

        cuenta.setSaldoInicial(nuevoSaldoFinal);
        cuentaRepository.save(cuenta);

        originalMovement.setFecha(LocalDateTime.now());
        originalMovement.setValor(nuevoValor);
        originalMovement.setSaldo(nuevoSaldoFinal);

        return movimientoRepository.save(originalMovement);
    }
}
