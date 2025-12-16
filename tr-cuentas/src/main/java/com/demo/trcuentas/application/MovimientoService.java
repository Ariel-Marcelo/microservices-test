package com.demo.trcuentas.application;

import com.demo.trcuentas.domain.dtos.cuenta.CuentaRepositoryPort;
import com.demo.trcuentas.domain.dtos.movimiento.MovimientoMapper;
import com.demo.trcuentas.domain.dtos.movimiento.MovimientoRepositoryPort;
import com.demo.trcuentas.domain.dtos.movimiento.MovimientoServicePort;
import com.demo.trcuentas.domain.dtos.movimiento.requests.MovimientoRequest;
import com.demo.trcuentas.domain.dtos.movimiento.responses.MovimientoResponse;
import com.demo.trcuentas.domain.exceptions.LowBalanceException;
import com.demo.trcuentas.domain.models.Cuenta;
import com.demo.trcuentas.domain.models.Movimiento;
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
public class MovimientoService implements MovimientoServicePort {

    private final CuentaRepositoryPort cuentaRepository;
    private final MovimientoRepositoryPort movimientoRepository;

    @Override
    public MovimientoResponse create(MovimientoRequest request) {
        log.info("INICIO TX CREATE: Procesando {} de {} en cuenta {}.", request.getTipoMovimiento(), request.getValor(), request.getNumeroCuenta());

        Cuenta cuenta = cuentaRepository
                .findActiveCuentasByNumeroId(request.getNumeroCuenta());
        
        log.debug("Estado inicial de la cuenta {}: Saldo {}", cuenta.getNumeroCuenta(), cuenta.getSaldoInicial());

        BigDecimal movementValue = request.getValor();
        BigDecimal currentAmount = cuenta.getSaldoInicial();
        BigDecimal lastAmount;

        if ("Debito".equalsIgnoreCase(request.getTipoMovimiento())) {
            movementValue = movementValue.negate();
            lastAmount = currentAmount.add(movementValue);
            log.debug("Cálculo Débito: Saldo inicial {} + Movimiento {} = Nuevo Saldo {}", currentAmount, movementValue, lastAmount);

            if (lastAmount.compareTo(BigDecimal.ZERO) < 0) {
                log.warn("FALLO CREATE: Saldo insuficiente. Intento de débito resultaría en saldo negativo: {}", lastAmount);
                throw new LowBalanceException("Saldo no disponible");
            }

        } else {
            lastAmount = currentAmount.add(movementValue);
            log.debug("Cálculo Crédito: Saldo inicial {} + Movimiento {} = Nuevo Saldo {}", currentAmount, movementValue, lastAmount);
        }

        Movimiento movimiento = MovimientoMapper.INSTANCE.toEntity(request);
        movimiento.setValor(movementValue);
        movimiento.setSaldo(lastAmount);
        movimiento.setCuenta(cuenta);

        cuenta.setSaldoInicial(lastAmount);
        cuentaRepository.save(cuenta);

        MovimientoResponse response = MovimientoMapper.INSTANCE
                .toResponse(movimientoRepository.save(movimiento));

        log.info("FIN TX CREATE: Movimiento ID {} creado. Saldo final de cuenta {} es {}.", response.getId(), cuenta.getNumeroCuenta(), lastAmount);
        return response;
    }

    @Override
    public List<MovimientoResponse> getAll() {
        return movimientoRepository.getAllMovimientos()
                .stream()
                .map(MovimientoMapper.INSTANCE::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MovimientoResponse getById(Long id) {
        log.debug("Buscando movimiento por ID: {}", id);
        return MovimientoMapper.INSTANCE
                .toResponse(movimientoRepository.getMovimientosById(id));
    }

    @Override
    public void delete(Long id) {
        log.warn("INICIO TX REVERSO: Solicitud de reversión para Movimiento ID: {}", id);
        
        Movimiento originalMovement = movimientoRepository.getMovimientosById(id);
        Cuenta cuenta = originalMovement.getCuenta();
        
        log.debug("Movimiento original: Tipo {}, Valor {}. Saldo actual de cuenta: {}", originalMovement.getTipoMovimiento(), originalMovement.getValor(), cuenta.getSaldoInicial());

        String nuevoTipo;
        if ("Debito".equalsIgnoreCase(originalMovement.getTipoMovimiento())) {
            nuevoTipo = "Credito";
        } else if ("Credito".equalsIgnoreCase(originalMovement.getTipoMovimiento())) {
            nuevoTipo = "Debito";
        } else {
            log.error("FALLO REVERSO: Intento de reversar movimiento ID {} que ya está en estado {}.", id, originalMovement.getTipoMovimiento());
            throw new IllegalArgumentException("No puede reversar una transacción que ya ha sido reversada");
        }

        BigDecimal balanceValue = originalMovement.getValor().negate();
        BigDecimal oldMovementValue = cuenta.getSaldoInicial().add(balanceValue);
        
        log.debug("Cálculo Reversión: Valor de reversión {}, Saldo resultante {}", balanceValue, oldMovementValue);

        if (oldMovementValue.compareTo(BigDecimal.ZERO) < 0) {
            log.warn("FALLO REVERSO: El saldo restante sería insuficiente después del reverso: {}", oldMovementValue);
            throw new LowBalanceException("Saldo insuficiente");
        }

        Movimiento balanceMovement = new Movimiento();
        balanceMovement.setFecha(java.time.LocalDateTime.now());
        balanceMovement.setTipoMovimiento(nuevoTipo);
        balanceMovement.setValor(balanceValue);
        balanceMovement.setSaldo(oldMovementValue);
        balanceMovement.setCuenta(cuenta);

        cuenta.setSaldoInicial(oldMovementValue);
        cuentaRepository.save(cuenta);
        originalMovement.setTipoMovimiento("Reversado");
        movimientoRepository.save(originalMovement);
        movimientoRepository.save(balanceMovement);
        
        log.warn("FIN TX REVERSO: Movimiento ID {} reversado. Se creó movimiento de contrapartida. Nuevo saldo: {}", id, oldMovementValue);
    }

    @Override
    public MovimientoResponse update(Long id, MovimientoRequest request) {
        log.warn("INICIO TX UPDATE: Solicitud de actualización para Movimiento ID: {} con nuevo valor {}", id, request.getValor());
        
        Movimiento originalMovement = movimientoRepository.getMovimientosById(id);
        Long cuentaId = originalMovement.getCuenta().getId();
        
        Movimiento ultimoMovimiento = movimientoRepository.findLastByCuentaId(cuentaId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontraron movimientos para validar."));

        if (!originalMovement.getId().equals(ultimoMovimiento.getId())) {
            log.error("FALLO UPDATE: Intento de editar movimiento ID {} cuando el último es ID {}.", id, ultimoMovimiento.getId());
            throw new IllegalArgumentException("Solo se permite editar el último movimiento de la cuenta para mantener la consistencia del saldo histórico.");
        }
        
        if ("Reversado".equalsIgnoreCase(originalMovement.getTipoMovimiento())) {
            log.error("FALLO UPDATE: Intento de editar movimiento ID {} que está reversado.", id);
            throw new IllegalArgumentException("No se puede editar un movimiento que ya ha sido reversado.");
        }

        if (!originalMovement.getTipoMovimiento().equalsIgnoreCase(request.getTipoMovimiento())) {
            log.error("FALLO UPDATE: Intento de cambiar tipo de movimiento de {} a {} en ID {}.", originalMovement.getTipoMovimiento(), request.getTipoMovimiento(), id);
            throw new IllegalArgumentException("No se permite cambiar el tipo de movimiento. Solo se puede ajustar el valor.");
        }

        Cuenta cuenta = originalMovement.getCuenta();
        
        BigDecimal saldoSinMovimientoPrevio = cuenta.getSaldoInicial().subtract(originalMovement.getValor());
        log.debug("Saldo previo al movimiento original: {}", saldoSinMovimientoPrevio);

        BigDecimal nuevoValor = request.getValor();
        if ("Debito".equalsIgnoreCase(request.getTipoMovimiento())) {
            nuevoValor = nuevoValor.negate();
        }

        BigDecimal nuevoSaldoFinal = saldoSinMovimientoPrevio.add(nuevoValor);
        log.debug("Nuevo saldo calculado: {}", nuevoSaldoFinal);

        if (nuevoSaldoFinal.compareTo(BigDecimal.ZERO) < 0) {
            log.warn("FALLO UPDATE: Saldo insuficiente. La actualización resultaría en saldo negativo: {}", nuevoSaldoFinal);
            throw new LowBalanceException("Saldo insuficiente para realizar esta actualización.");
        }

        cuenta.setSaldoInicial(nuevoSaldoFinal);
        cuentaRepository.save(cuenta);

        originalMovement.setFecha(java.time.LocalDateTime.now());
        originalMovement.setValor(nuevoValor);
        originalMovement.setSaldo(nuevoSaldoFinal);

        MovimientoResponse response = MovimientoMapper.INSTANCE.toResponse(movimientoRepository.save(originalMovement));
        
        log.warn("FIN TX UPDATE: Movimiento ID {} actualizado. Nuevo Saldo final de cuenta {} es {}.", id, cuenta.getNumeroCuenta(), nuevoSaldoFinal);
        return response;
    }
}