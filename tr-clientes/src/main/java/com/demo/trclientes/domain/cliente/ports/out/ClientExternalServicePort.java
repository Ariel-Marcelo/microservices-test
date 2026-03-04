package com.demo.trclientes.domain.cliente.ports.out;

import com.demo.trclientes.domain.cliente.models.Client;

public interface ClientExternalServicePort {
    void notifyCreate(Client client);
    void notifyUpdate(Long id, Client client);
    void notifyDelete(Long id);
}