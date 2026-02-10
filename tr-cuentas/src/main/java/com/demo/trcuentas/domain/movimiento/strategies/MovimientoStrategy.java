package com.demo.trcuentas.domain.movimiento.strategies;

import java.math.BigDecimal;

public interface MovimientoStrategy {
    String getTipoMovimiento();
    BigDecimal calcularNuevoSaldo(BigDecimal saldoActual, BigDecimal valor);
}
