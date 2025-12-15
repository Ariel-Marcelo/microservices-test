package com.demo.trclientes.domain.dtos.cliente.requests;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String genero;

    @Min(value = 18, message = "Debe ser mayor de edad")
    private int edad;

    @NotBlank(message = "La identificación es obligatoria")
    private String identificacion;

    private String direccion;

    @Pattern(regexp = "^[0-9]{10}$|^$", message = "El teléfono debe tener 10 dígitos")
    private String telefono;

    @NotBlank(message = "El ClienteID es obligatorio")
    private String clienteId;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasenia;

    private Boolean estado;
}
