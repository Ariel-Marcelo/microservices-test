package com.demo.trcuentas.domain.dtos.reporte.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ReporteCuenta {
    private String numeroCuenta;
    private String tipoCuenta;
    private BigDecimal saldoActual;
    private boolean estado;
    private List<ReporteMovimiento> movimientos;
}
