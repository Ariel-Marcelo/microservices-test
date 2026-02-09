package com.demo.trcuentas.domain.movimiento;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class MovimientoRequestDomain {
    private String numeroCuenta;
    private String tipoMovimiento;
    private BigDecimal valor;
}
