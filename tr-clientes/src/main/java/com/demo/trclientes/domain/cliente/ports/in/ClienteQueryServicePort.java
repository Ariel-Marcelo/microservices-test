package com.demo.trclientes.domain.cliente.ports.in;

import com.demo.trclientes.domain.cliente.models.Client;
import java.util.List;

public interface ClienteQueryServicePort {
    List<Client> getAll();
    Client getById(Long id);
}
