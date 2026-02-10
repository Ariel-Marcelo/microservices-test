package com.demo.trclientes.domain.cliente.ports.out;

import com.demo.trclientes.domain.cliente.models.Client;
import java.util.List;

public interface ClienteRepositoryPort {

    Client save(Client client);

    List<Client> getAllActiveClients();

    Client getActiveClientById(Long id);

    Client getActiveClientByUniqueId(String id);

}
