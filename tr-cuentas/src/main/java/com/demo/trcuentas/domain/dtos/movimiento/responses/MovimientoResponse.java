package com.demo.trcuentas.domain.dtos.movimiento.responses;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MovimientoResponse {
    private Long id;
    private String numeroCuenta;
    private String tipoCuenta;

    private LocalDateTime fecha;
    private boolean estado;

    private String tipoMovimiento;

    private BigDecimal valor;

    private BigDecimal saldo;
}