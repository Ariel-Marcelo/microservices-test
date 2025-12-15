package com.demo.trcuentas.infrastructure.repositories.cuenta;

import com.demo.trcuentas.domain.models.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CuentaJpaRepository extends JpaRepository<Cuenta, Long> {
    List<Cuenta> findByEstadoTrue();

    Optional<Cuenta> findByIdAndEstadoTrue(Long id);

    Optional<Cuenta> findByNumeroCuentaAndEstadoTrue(String numeroCuenta);

    List<Cuenta> findByCliente_ClienteId(String clienteId);

}
