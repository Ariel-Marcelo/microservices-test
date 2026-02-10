package com.demo.trcuentas.domain.movimiento.strategies;

import com.demo.trcuentas.domain.exceptions.LowBalanceException;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DebitoStrategy implements MovimientoStrategy {
    @Override
    public String getTipoMovimiento() {
        return "Debito";
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
