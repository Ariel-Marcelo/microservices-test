package com.demo.trcuentas.domain.clienteCuenta;

import com.demo.trcuentas.domain.dtos.ClienteRequest;

public interface ClienteReplicaService {
    void saveReplica(ClienteRequest dto);

    void updateReplica(Long id, ClienteRequest dto);

    void deleteReplica(Long id);
}
