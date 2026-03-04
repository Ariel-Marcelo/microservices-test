package com.demo.trcuentas.domain.movimiento.strategies;

import com.demo.trcuentas.infrastructure.adapters.in.rest.dtos.TipoMovimiento;

import java.math.BigDecimal;

public interface MovimientoStrategy {
    TipoMovimiento getTipoMovimiento();
    BigDecimal calcularNuevoSaldo(BigDecimal saldoActual, BigDecimal valor);
}
