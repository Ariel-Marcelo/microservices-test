package com.demo.trcuentas.infrastructure.persistence.models;

import com.demo.trcuentas.infrastructure.adapters.in.rest.dtos.TipoMovimiento;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "movimientos")
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    private TipoMovimiento tipoMovimiento;

    @NotNull
    private BigDecimal valor;

    @NotNull
    private BigDecimal saldo;

    @ManyToOne
    private Cuenta cuenta;
}
