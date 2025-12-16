package com.demo.trcuentas.domain.dtos.clienteCuenta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteReplica {
    private Long id;
    private String clienteId;
    private String nombre;
    private boolean estado;
}