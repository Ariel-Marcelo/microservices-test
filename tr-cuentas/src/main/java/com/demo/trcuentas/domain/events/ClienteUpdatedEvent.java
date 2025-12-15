package com.demo.trcuentas.domain.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteUpdatedEvent {
    private Long id;
    private String clienteId;
    private String nombre;
}
