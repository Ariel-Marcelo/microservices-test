package com.demo.trcuentas.domain.movimiento.strategies;

import com.demo.trcuentas.infrastructure.adapters.in.rest.dtos.TipoMovimiento;
import com.demo.trcuentas.domain.shared.exceptions.LowBalanceException;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DebitoStrategy implements MovimientoStrategy {
    @Override
    public TipoMovimiento getTipoMovimiento() {
        return TipoMovimiento.DEBITO;
    }

    @Override
    public BigDecimal calcularNuevoSaldo(BigDecimal saldoActual, BigDecimal valor) {
        BigDecimal nuevoSaldo = saldoActual.subtract(valor);
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new LowBalanceException("Saldo no disponible");
        }
        return nuevoSaldo;
    }
}

