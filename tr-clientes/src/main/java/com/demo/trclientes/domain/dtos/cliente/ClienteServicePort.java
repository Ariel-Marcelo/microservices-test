package com.demo.trclientes.domain.dtos.cliente;

import com.demo.trclientes.domain.dtos.generated.ClienteRequest;
import com.demo.trclientes.domain.dtos.generated.ClienteResponse;

import java.util.List;

public interface ClienteServicePort {

    ClienteResponse create(ClienteRequest clienteRequest);

    List<ClienteResponse> getAll();

    ClienteResponse getById(Long id);

    ClienteResponse update(Long id, ClienteRequest clienteRequest);

    void delete(Long id);
}
