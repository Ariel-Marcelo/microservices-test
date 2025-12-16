package com.demo.trcuentas.application;

import com.demo.trcuentas.domain.dtos.clienteCuenta.ClienteRequest;
import com.demo.trcuentas.domain.dtos.clienteCuenta.ClienteReplicaService;
import com.demo.trcuentas.domain.models.ClienteCuenta;
import com.demo.trcuentas.infrastructure.repositories.cliente.ClienteCuentaJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteCuentaService implements ClienteReplicaService {

    private final ClienteCuentaJpaRepository repository;

    @Transactional
    public void saveReplica(ClienteRequest dto) {
        log.info("REPLICA REST: Recibiendo creación/actualización para cliente ID: {}", dto.getId());

        ClienteCuenta cliente = new ClienteCuenta();
        cliente.setId(dto.getId());
        cliente.setClienteId(dto.getClienteId());
        cliente.setNombre(dto.getNombre());
        cliente.setEstado(true);

        repository.save(cliente);
        log.info("Replica guardada correctamente.");
    }

    @Transactional
    public void updateReplica(Long id, ClienteRequest dto) {
        log.info("REPLICA REST: Actualizando cliente ID: {}", id);

        ClienteCuenta cliente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente Replica no encontrado con ID: " + id));

        cliente.setClienteId(dto.getClienteId());
        cliente.setNombre(dto.getNombre());

        repository.save(cliente);
    }

    @Transactional
    public void deleteReplica(Long id) {
        log.info("REPLICA REST: Inhabilitando cliente ID: {}", id);

        ClienteCuenta cliente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente Replica no encontrado con ID: " + id));

        cliente.setEstado(false);
        repository.save(cliente);
    }
}
