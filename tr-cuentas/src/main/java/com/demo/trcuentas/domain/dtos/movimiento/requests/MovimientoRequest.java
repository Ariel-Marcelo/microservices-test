package com.demo.trcuentas.domain.dtos.movimiento.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MovimientoRequest {

    @NotBlank(message = "El número de cuenta es obligatorio")
    private String numeroCuenta;

    @NotBlank(message = "El tipo de movimiento es obligatorio (Credito/Debito)")
    private String tipoMovimiento;

    @NotNull(message = "El valor es obligatorio")
    @Positive(message = "El valor del movimiento debe ser siempre positivo")
    private BigDecimal valor;
}