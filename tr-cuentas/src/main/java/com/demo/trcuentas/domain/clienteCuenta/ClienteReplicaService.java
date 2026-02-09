package com.demo.trcuentas.domain.clienteCuenta;

public interface ClienteReplicaService {
    void saveReplica(ClienteRequestDomain domain);
    void updateReplica(Long id, ClienteRequestDomain domain);
    void deleteReplica(Long id);
}