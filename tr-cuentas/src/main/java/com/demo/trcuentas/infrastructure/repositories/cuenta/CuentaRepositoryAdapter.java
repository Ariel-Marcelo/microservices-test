package com.demo.trcuentas.infrastructure.repositories.cuenta;

import com.demo.trcuentas.domain.dtos.cuenta.CuentaRepositoryPort;
import com.demo.trcuentas.domain.models.Cuenta;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class CuentaRepositoryAdapter implements CuentaRepositoryPort {

    private final CuentaJpaRepository jpaRepository;
    @Override
    public Cuenta save(Cuenta cuenta) {
        return jpaRepository.save(cuenta);
    }

    @Override
    public List<Cuenta> getAllActiveCuentas() {
        return jpaRepository.findByEstadoTrue();
    }

    @Override
    public Cuenta getActiveCuentasById(Long id) {
        return jpaRepository.findByIdAndEstadoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Cuenta no encontrado o inactiva con ID: " + id));
    }

    @Override
    public Cuenta findActiveCuentasByNumeroId(String numeroCuenta) {
        return jpaRepository.findByNumeroCuentaAndEstadoTrue(numeroCuenta)
                .orElseThrow(() -> new EntityNotFoundException("Cuenta no encontrado o inactiva con el número: " + numeroCuenta));
    }

    @Override
    public List<Cuenta> getCuentasByCliente(String clienteId) {
        return jpaRepository.findByCliente_ClienteId(clienteId);
    }
}
