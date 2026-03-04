package com.demo.trclientes.domain.cliente.ports.in;

import com.demo.trclientes.domain.cliente.models.Client;

public interface ClientCommandServicePort {
    Client create(Client client);
    Client update(Long id, Client client);
    void delete(Long id);
}
