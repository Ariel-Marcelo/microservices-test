package com.demo.trclientes.application.cliente;

import com.demo.trclientes.domain.cliente.ports.ClienteExternalServicePort;
import com.demo.trclientes.domain.cliente.ports.ClienteRepositoryPort;
import com.demo.trclientes.domain.cliente.ports.ClienteServicePort;
import com.demo.trclientes.domain.cliente.models.Client;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ClienteService implements ClienteServicePort {

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
        client.setEstado(false);
        repository.save(client);
        
        log.warn("CLIENTE INHABILITADO: Cliente ID {} marcado como INACTIVO en BD local.", id);

        externalService.notifyDelete(id);

        log.error("FIN DELETE CLIENTE: Cliente ID {} inhabilitado y réplica enviada. Transacción completada.", id);
    }

    @Override
    public List<Client> getAll() {
        log.info("INICIO GET ALL CLIENTES: Recuperando todos los clientes activos.");

        List<Client> clients = repository.getAllActiveClients();

        log.info("FIN GET ALL CLIENTES: {} clientes activos recuperados.", clients.size());
        return clients;
    }

    @Override
    public Client getById(Long id) {
        log.info("INICIO GET BY ID CLIENTE: Buscando cliente activo por ID {}.", id);
        
        Client client = repository.getActiveClientById(id);
        
        log.info("FIN GET BY ID CLIENTE: Cliente ID {} encontrado.", id);
        return client;
    }

}
