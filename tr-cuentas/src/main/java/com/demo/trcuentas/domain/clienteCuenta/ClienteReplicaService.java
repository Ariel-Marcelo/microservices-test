package com.demo.trcuentas.domain.clienteCuenta;

public interface ClienteReplicaService {
    void saveReplica(ClienteRequest dto);

    void updateReplica(Long id, ClienteRequest dto);

    void deleteReplica(Long id);
}
