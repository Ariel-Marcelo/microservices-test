package com.demo.trcuentas.application;

import com.demo.trcuentas.domain.clienteCuenta.ClienteDomain;
import com.demo.trcuentas.domain.clienteCuenta.ClienteReplicaService;
import com.demo.trcuentas.domain.clienteCuenta.ClienteReplicaRepositoryPort;
import com.demo.trcuentas.domain.clienteCuenta.ClienteRequestDomain;
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
    public void saveReplica(ClienteRequestDomain domain) {
        log.info("REPLICA: Procesando creación/actualización para cliente ID: {}", domain.getId());

        ClienteDomain clienteDomain = ClienteDomain.builder()
                .id(domain.getId())
                .clienteId(domain.getClienteId())
                .nombre(domain.getNombre())
                .estado(true)
                .build();

        repository.save(clienteDomain);
        log.info("Replica guardada correctamente.");
    }

    @Transactional
    public void updateReplica(Long id, ClienteRequestDomain domain) {
        log.info("REPLICA: Actualizando cliente ID: {}", id);

        ClienteDomain cliente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente Replica no encontrado con ID: " + id));

        cliente.setClienteId(domain.getClienteId());
        cliente.setNombre(domain.getNombre());

        repository.save(cliente);
    }

    @Transactional
    public void deleteReplica(Long id) {
        log.info("REPLICA: Inhabilitando cliente ID: {}", id);

        ClienteDomain cliente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente Replica no encontrado con ID: " + id));

        cliente.setEstado(false);
        repository.save(cliente);
    }
}
