package com.demo.trcuentas.domain.clienteCuenta;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClienteRequestDomain {
    private Long id;
    private String clienteId;
    private String nombre;
    private Boolean estado;
}
