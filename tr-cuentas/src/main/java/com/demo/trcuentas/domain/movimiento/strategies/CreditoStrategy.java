package com.demo.trcuentas.domain.movimiento.strategies;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class CreditoStrategy implements MovimientoStrategy {
    @Override
    public String getTipoMovimiento() {
        return "Credito";
    }

    @Override
    public BigDecimal calcularNuevoSaldo(BigDecimal saldoActual, BigDecimal valor) {
        return saldoActual.add(valor);
    }
}
