package com.demo.trcuentas.domain.clienteCuenta;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClienteDomain {
    private Long id;
    private String clienteId;
    private String nombre;
    private boolean estado;
}
