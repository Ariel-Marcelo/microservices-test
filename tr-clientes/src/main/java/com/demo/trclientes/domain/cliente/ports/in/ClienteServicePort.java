package com.demo.trclientes.domain.cliente.ports.in;

import com.demo.trclientes.domain.cliente.models.Client;

import java.util.List;

public interface ClienteServicePort {

    Client create(Client client);

    List<Client> getAll();

    Client getById(Long id);

    Client update(Long id, Client client);

    void delete(Long id);
}
