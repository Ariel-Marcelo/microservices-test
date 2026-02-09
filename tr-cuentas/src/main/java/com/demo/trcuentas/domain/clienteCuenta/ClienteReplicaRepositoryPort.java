package com.demo.trcuentas.domain.clienteCuenta;

import java.util.Optional;

public interface ClienteReplicaRepositoryPort {
    Optional<ClienteDomain> findById(Long id);
    Optional<ClienteDomain> findByIdAndEstadoTrue(Long id);
    Optional<ClienteDomain> findByClienteId(String clienteId);
    ClienteDomain save(ClienteDomain cliente);
    void deleteById(Long id);
}