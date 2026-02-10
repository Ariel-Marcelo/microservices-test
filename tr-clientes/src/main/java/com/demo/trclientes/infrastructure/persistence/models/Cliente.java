package com.demo.trclientes.infrastructure.persistence.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "clientes")
public class Cliente extends Persona {

    @Column(unique = true, nullable = false)
    private String clienteId;

    @NotBlank
    private String contrasenia;

    private boolean estado;
}
