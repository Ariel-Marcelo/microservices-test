package com.demo.trclientes.domain.dtos.cliente;

import com.demo.trclientes.domain.models.Cliente;
import java.util.List;

public interface ClienteRepositoryPort {

    Cliente save(Cliente cliente);

    List<Cliente> getAllActiveClients();

    Cliente getActiveClientById(Long id);

    Cliente getActiveClientByUniqueId(String id);

}
