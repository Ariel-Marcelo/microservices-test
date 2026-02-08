package com.demo.trclientes.infrastructure.adapters.out.persistence.cliente;

import com.demo.trclientes.infrastructure.adapters.out.persistence.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteJpaRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByEstadoTrue();

    Optional<Cliente> findByIdAndEstadoTrue(Long id);

    Optional<Cliente> findByClienteId(String clientId);

}
