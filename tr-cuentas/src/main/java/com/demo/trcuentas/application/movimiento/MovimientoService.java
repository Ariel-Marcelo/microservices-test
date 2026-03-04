package com.demo.trcuentas.application.movimiento;

import com.demo.trcuentas.domain.cuenta.CuentaDomain;
import com.demo.trcuentas.domain.cuenta.ports.out.CuentaRepositoryPort;
import com.demo.trcuentas.domain.movimiento.MovimientoDomain;
import com.demo.trcuentas.infrastructure.adapters.in.rest.dtos.TipoMovimiento;
import com.demo.trcuentas.domain.movimiento.ports.out.MovimientoRepositoryPort;
import com.demo.trcuentas.domain.movimiento.ports.in.MovimientoServicePort;
import com.demo.trcuentas.domain.movimiento.strategies.MovimientoStrategy;
import com.demo.trcuentas.domain.movimiento.strategies.MovimientoStrategyFactory;
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
    private final MovimientoStrategyFactory strategyFactory;

    @Override
    public MovimientoDomain create(MovimientoDomain domain) {
        log.info("INICIO TX CREATE: Procesando {} de {} en cuenta {}.",
                domain.getTipoMovimiento(), domain.getValor(), domain.getCuentaId());

        CuentaDomain cuenta = cuentaRepository.getActiveCuentasById(domain.getCuentaId());

        MovimientoStrategy strategy = strategyFactory.getStrategy(domain.getTipoMovimiento());
        BigDecimal nuevoSaldo = strategy.calcularNuevoSaldo(cuenta.getSaldoInicial(), domain.getValor());

        BigDecimal valorFinal = TipoMovimiento.DEBITO.equals(domain.getTipoMovimiento())
                ? domain.getValor().negate()
                : domain.getValor();

        domain.setFecha(LocalDateTime.now());
        domain.setValor(valorFinal);
        domain.setSaldo(nuevoSaldo);
        domain.setCuentaId(cuenta.getId());

        cuenta.setSaldoInicial(nuevoSaldo);
        cuentaRepository.save(cuenta);

        return movimientoRepository.save(domain);
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
        log.warn("INICIO TX REVERSO: Movimiento ID: {}", id);

        MovimientoDomain original = movimientoRepository.getMovimientosById(id);
        CuentaDomain cuenta = cuentaRepository.getActiveCuentasById(original.getCuentaId());

        TipoMovimiento nuevoTipo;
        if (TipoMovimiento.DEBITO.equals(original.getTipoMovimiento()) || original.getValor().compareTo(BigDecimal.ZERO) < 0) {
            nuevoTipo = TipoMovimiento.CREDITO;
        } else if (TipoMovimiento.CREDITO.equals(original.getTipoMovimiento()) || original.getValor().compareTo(BigDecimal.ZERO) > 0) {
            nuevoTipo = TipoMovimiento.DEBITO;
        } else {
            throw new IllegalArgumentException("No puede reversar esta transacción");
        }

        BigDecimal valorReverso = original.getValor().negate();

        MovimientoStrategy strategy = strategyFactory.getStrategy(nuevoTipo);
        BigDecimal saldoFinal = strategy.calcularNuevoSaldo(cuenta.getSaldoInicial(), valorReverso.abs());

        MovimientoDomain reverso = MovimientoDomain.builder()
                .fecha(LocalDateTime.now())
                .tipoMovimiento(nuevoTipo)
                .valor(valorReverso)
                .saldo(saldoFinal)
                .cuentaId(cuenta.getId())
                .build();

        cuenta.setSaldoInicial(saldoFinal);
        cuentaRepository.save(cuenta);

        original.setTipoMovimiento(TipoMovimiento.REVERSADO);
        movimientoRepository.save(original);
        movimientoRepository.save(reverso);
    }

    @Override
    public MovimientoDomain update(Long id, MovimientoDomain domain) {
        log.warn("INICIO TX UPDATE: Movimiento ID: {}", id);

        MovimientoDomain original = movimientoRepository.getMovimientosById(id);

        MovimientoDomain ultimo = movimientoRepository.findLastByCuentaId(original.getCuentaId())
                .orElseThrow(() -> new EntityNotFoundException("No se encontraron movimientos."));

        if (!original.getId().equals(ultimo.getId())) {
            throw new IllegalArgumentException("Solo se permite editar el último movimiento.");
        }

        CuentaDomain cuenta = cuentaRepository.getActiveCuentasById(original.getCuentaId());
        BigDecimal saldoBase = cuenta.getSaldoInicial().subtract(original.getValor());

        MovimientoStrategy strategy = strategyFactory.getStrategy(domain.getTipoMovimiento());
        BigDecimal nuevoSaldo = strategy.calcularNuevoSaldo(saldoBase, domain.getValor());

        BigDecimal nuevoValor = TipoMovimiento.DEBITO.equals(domain.getTipoMovimiento())
                ? domain.getValor().negate()
                : domain.getValor();

        cuenta.setSaldoInicial(nuevoSaldo);
        cuentaRepository.save(cuenta);

        original.setFecha(LocalDateTime.now());
        original.setValor(nuevoValor);
        original.setSaldo(nuevoSaldo);

        return movimientoRepository.save(original);
    }
}

