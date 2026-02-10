package com.demo.trcuentas.domain.clienteCuenta.ports.in;

import com.demo.trcuentas.domain.clienteCuenta.ClienteDomain;

public interface ClienteReplicaService {
    void saveReplica(ClienteDomain domain);
    void updateReplica(Long id, ClienteDomain domain);
    void deleteReplica(Long id);
}
