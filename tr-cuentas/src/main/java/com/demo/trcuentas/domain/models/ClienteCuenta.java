package com.demo.trcuentas.domain.models;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "cliente_cuentas")
@Data
public class ClienteCuenta {
    @Id
    private Long id;

    @Column(unique = true)
    private String clienteId;

    private String nombre;

    private boolean estado;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cuenta> cuentas;
}
