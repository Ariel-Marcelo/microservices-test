package com.demo.trcuentas.application;

import com.demo.trcuentas.domain.clienteCuenta.ClienteDomain;
import com.demo.trcuentas.domain.clienteCuenta.ClienteReplicaService;
import com.demo.trcuentas.domain.clienteCuenta.ClienteReplicaRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteCuentaService implements ClienteReplicaService {

    private final ClienteReplicaRepositoryPort repository;

    @Transactional
    @Override
    public void saveReplica(ClienteDomain domain) {
        log.info("REPLICA: Procesando persistencia para cliente ID: {}", domain.getId());
        repository.save(domain);
        log.info("Replica de cliente {} sincronizada.", domain.getNombre());
    }

    @Transactional
    @Override
    public void updateReplica(Long id, ClienteDomain domain) {
        log.info("REPLICA: Actualizando cliente ID: {}", id);
        
        if (!repository.findById(id).isPresent()) {
            throw new EntityNotFoundException("Cliente Replica no encontrado con ID: " + id);
        }

        domain.setId(id);
        repository.save(domain);
    }

    @Transactional
    @Override
    public void deleteReplica(Long id) {
        log.info("REPLICA: Marcando como inactivo cliente ID: {}", id);

        ClienteDomain cliente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente Replica no encontrado con ID: " + id));

        cliente.setEstado(false);
        repository.save(cliente);
    }
}
