package com.demo.trclientes.domain.cliente.ports;

import com.demo.trclientes.infrastructure.adapters.in.rest.dtos.ClienteRequest;
import com.demo.trclientes.infrastructure.adapters.in.rest.dtos.ClienteResponse;

import java.util.List;

public interface ClienteServicePort {

    ClienteResponse create(ClienteRequest clienteRequest);

    List<ClienteResponse> getAll();

    ClienteResponse getById(Long id);

    ClienteResponse update(Long id, ClienteRequest clienteRequest);

    void delete(Long id);
}
