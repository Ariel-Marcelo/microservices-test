package com.demo.trcuentas.infrastructure.adapters.out.cliente;

import com.demo.trcuentas.infrastructure.persistence.models.ClienteCuenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteCuentaJpaRepository extends JpaRepository<ClienteCuenta, Long> {

    Optional<ClienteCuenta> findByIdAndEstadoTrue(Long id);

    Optional<ClienteCuenta> findByClienteId(String clientId);
}
