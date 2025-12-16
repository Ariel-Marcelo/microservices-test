package com.demo.trcuentas.domain.dtos.clienteCuenta;

public interface ClienteReplicaService {
    void saveReplica(ClienteReplica dto);

    void updateReplica(Long id, ClienteReplica dto);

    void deleteReplica(Long id);
}
