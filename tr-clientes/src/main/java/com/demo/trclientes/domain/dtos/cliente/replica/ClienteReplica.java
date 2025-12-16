package com.demo.trclientes.domain.dtos.cliente.replica;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClienteReplica {
    private Long id;
    private String clienteId;
    private String nombre;
    private boolean estado;
}
