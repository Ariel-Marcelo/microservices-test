package com.demo.trclientes.application.cliente;

import com.demo.trclientes.domain.cliente.models.Client;
import com.demo.trclientes.domain.cliente.ports.in.ClienteQueryServicePort;
import com.demo.trclientes.domain.cliente.ports.out.ClienteRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteQueryService implements ClienteQueryServicePort {

    private final ClienteRepositoryPort repository;

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
