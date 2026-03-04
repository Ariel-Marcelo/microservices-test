package com.demo.trcuentas.domain.movimiento.strategies;

import com.demo.trcuentas.infrastructure.adapters.in.rest.dtos.TipoMovimiento;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class CreditoStrategy implements MovimientoStrategy {
    @Override
    public TipoMovimiento getTipoMovimiento() {
        return TipoMovimiento.CREDITO;
    }

    @Override
    public BigDecimal calcularNuevoSaldo(BigDecimal saldoActual, BigDecimal valor) {
        return saldoActual.add(valor);
    }
}

