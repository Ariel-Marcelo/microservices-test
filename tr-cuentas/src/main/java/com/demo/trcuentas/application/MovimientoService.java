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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MovimientoService implements MovimientoServicePort {

    private final CuentaRepositoryPort cuentaRepository;
    private final MovimientoRepositoryPort movimientoRepository;

    @Override
    public MovimientoResponse create(MovimientoRequest request) {

        Cuenta cuenta = cuentaRepository
                .findActiveCuentasByNumeroId(request.getNumeroCuenta());

        BigDecimal movementValue = request.getValor();
        BigDecimal currentAmount = cuenta.getSaldoInicial();
        BigDecimal lastAmount;

        if ("Debito".equalsIgnoreCase(request.getTipoMovimiento())) {
            movementValue = movementValue.negate();
            lastAmount = currentAmount.add(movementValue);

            if (lastAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new LowBalanceException("Saldo no disponible");
            }

        } else {
            lastAmount = currentAmount.add(movementValue);
        }

        Movimiento movimiento = MovimientoMapper.INSTANCE.toEntity(request);
        movimiento.setValor(movementValue);
        movimiento.setSaldo(lastAmount);
        movimiento.setCuenta(cuenta);

        cuenta.setSaldoInicial(lastAmount);
        cuentaRepository.save(cuenta);

        return MovimientoMapper.INSTANCE
                .toResponse(movimientoRepository.save(movimiento));
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
        return MovimientoMapper.INSTANCE
                .toResponse(movimientoRepository.getMovimientosById(id));
    }

    @Override
    public void delete(Long id) {
        Movimiento originalMovement = movimientoRepository.getMovimientosById(id);
        Cuenta cuenta = originalMovement.getCuenta();

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
    }

    @Override
    public MovimientoResponse update(Long id, MovimientoRequest request) {
        Movimiento originalMovement = movimientoRepository.getMovimientosById(id);
        Long cuentaId = originalMovement.getCuenta().getId();

        Movimiento ultimoMovimiento = movimientoRepository.findLastByCuentaId(cuentaId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontraron movimientos para validar."));

        if (!originalMovement.getId().equals(ultimoMovimiento.getId())) {
            throw new IllegalArgumentException("Solo se permite editar el último movimiento de la cuenta para mantener la consistencia del saldo histórico.");
        }

        if ("Reversado".equalsIgnoreCase(originalMovement.getTipoMovimiento())) {
            throw new IllegalArgumentException("No se puede editar un movimiento que ya ha sido reversado.");
        }

        if (!originalMovement.getTipoMovimiento().equalsIgnoreCase(request.getTipoMovimiento())) {
            throw new IllegalArgumentException("No se permite cambiar el tipo de movimiento. Solo se puede ajustar el valor.");
        }

        Cuenta cuenta = originalMovement.getCuenta();

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

        originalMovement.setFecha(java.time.LocalDateTime.now());
        originalMovement.setValor(nuevoValor);
        originalMovement.setSaldo(nuevoSaldoFinal);

        return MovimientoMapper.INSTANCE.toResponse(movimientoRepository.save(originalMovement));
    }
}
