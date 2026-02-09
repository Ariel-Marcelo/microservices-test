package com.demo.trcuentas.domain.reporte;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CuentaReporte {
    private String numeroCuenta;
    private String tipoCuenta;
    private BigDecimal saldoActual;
    private Boolean estado;
    private List<MovimientoReporte> movimientos;
}
