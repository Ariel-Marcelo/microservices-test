package com.demo.trcuentas.domain.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteCreatedEvent implements Serializable {
    private Long id;
    private String clienteId;
    private String nombre;
}