package com.demo.trclientes.application.cliente;

import com.demo.trclientes.domain.cliente.models.Client;
import com.demo.trclientes.domain.cliente.ports.in.ClienteCommandServicePort;
import com.demo.trclientes.domain.cliente.ports.out.ClienteExternalServicePort;
import com.demo.trclientes.domain.cliente.ports.out.ClienteRepositoryPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ClienteCommandService implements ClienteCommandServicePort {

    private final ClienteRepositoryPort repository;
    private final ClienteExternalServicePort externalService;

    @Override
    public Client create(Client clientDomain) {
        log.info("INICIO CREATE CLIENTE: Creando cliente con Identificación: {}", clientDomain.getIdentificacion());
        Client savedClient = repository.save(clientDomain);

        log.info("CLIENTE GUARDADO: Cliente ID {} guardado en BD local. Preparando réplica...", savedClient.getId());
        externalService.notifyCreate(savedClient);

        log.info("FIN CREATE CLIENTE: Cliente ID {} creado y replicación exitosa. Transacción completada.", savedClient.getId());
        return savedClient;
    }

    @Override
    public Client update(Long id, Client clientDomain) {
        log.warn("INICIO UPDATE CLIENTE: Actualizando Cliente ID {}. Identificación: {}", id, clientDomain.getIdentificacion());

        Client existingClient = repository.getActiveClientById(id);
        
        clientDomain.setId(existingClient.getId());
        Client updatedClient = repository.save(clientDomain);
        
        log.debug("CLIENTE ACTUALIZADO: Datos de Cliente ID {} guardados en BD local.", id);

        externalService.notifyUpdate(updatedClient.getId(), updatedClient);

        log.warn("FIN UPDATE CLIENTE: Cliente ID {} actualizado y réplica enviada. Transacción completada.", id);
        return updatedClient;
    }

    @Override
    public void delete(Long id) {
        log.error("INICIO DELETE CLIENTE: Solicitud de ELIMINACIÓN LÓGICA para Cliente ID {}.", id);

        Client client = repository.getActiveClientById(id);
        client.inactivate();

        repository.save(client);
        log.info("CLIENTE INHABILITADO: Cliente ID {} marcado como INACTIVO en BD local.", id);

        externalService.notifyDelete(id);
        log.info("FIN DELETE CLIENTE: Cliente ID {} inhabilitado y réplica enviada. Transacción completada.", id);
    }
}
