package com.demo.trcuentas.domain.movimiento;

import com.demo.trcuentas.infrastructure.adapters.in.rest.dtos.TipoMovimiento;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class MovimientoDomain {
    private Long id;
    private LocalDateTime fecha;
    private TipoMovimiento tipoMovimiento;
    private BigDecimal valor;
    private BigDecimal saldo;
    private Long cuentaId;
}
