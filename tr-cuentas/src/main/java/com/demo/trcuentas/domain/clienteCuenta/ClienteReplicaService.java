package com.demo.trcuentas.domain.clienteCuenta;

public interface ClienteReplicaService {
    void saveReplica(ClienteDomain domain);
    void updateReplica(Long id, ClienteDomain domain);
    void deleteReplica(Long id);
}
