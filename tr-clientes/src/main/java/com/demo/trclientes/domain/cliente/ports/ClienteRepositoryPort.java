package com.demo.trclientes.domain.cliente.ports;

import com.demo.trclientes.infrastructure.adapters.out.persistence.models.Cliente;
import java.util.List;

public interface ClienteRepositoryPort {

    Cliente save(Cliente cliente);

    List<Cliente> getAllActiveClients();

    Cliente getActiveClientById(Long id);

    Cliente getActiveClientByUniqueId(String id);

}
