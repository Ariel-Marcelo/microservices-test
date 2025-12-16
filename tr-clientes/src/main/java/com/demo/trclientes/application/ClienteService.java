package com.demo.trclientes.application;

import com.demo.trclientes.domain.dtos.cliente.ClienteMapper;
import com.demo.trclientes.domain.dtos.cliente.ClienteRepositoryPort;
import com.demo.trclientes.domain.dtos.cliente.ClienteServicePort;
import com.demo.trclientes.domain.dtos.cliente.replica.ClienteReplica;
import com.demo.trclientes.domain.dtos.cliente.requests.ClienteRequest;
import com.demo.trclientes.domain.dtos.cliente.responses.ClienteResponse;
import com.demo.trclientes.domain.models.Cliente;
import com.demo.trclientes.infrastructure.repositories.cliente.api.CuentaRestClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteService implements ClienteServicePort {

    private final ClienteRepositoryPort repository;
    private final CuentaRestClient cuentaRestClient;

    @Override
    public ClienteResponse create(ClienteRequest clienteRequest) {
        var user = ClienteMapper.INSTANCE.toEntity(clienteRequest);
        Cliente cliente = repository.save(user);
        ClienteReplica replica = ClienteReplica.builder()
                .id(cliente.getId())
                .clienteId(cliente.getClienteId())
                .nombre(cliente.getNombre())
                .build();
        cuentaRestClient.notifyCreate(replica);
        return ClienteMapper.INSTANCE.toResponse(cliente);
    }

    @Override
    public ClienteResponse update(Long id, ClienteRequest clienteRequest) {
        Cliente client = repository.getActiveClientById(id);
        ClienteMapper.INSTANCE.updateEntityFromRequest(clienteRequest, client);
        Cliente updatedClient = repository.save(client);

        ClienteReplica replica = ClienteReplica.builder()
                .id(updatedClient.getId())
                .clienteId(updatedClient.getClienteId())
                .nombre(updatedClient.getNombre())
                .build();
        cuentaRestClient.notifyUpdate(updatedClient.getId(), replica);
        return ClienteMapper.INSTANCE.toResponse(updatedClient);
    }

    @Override
    public void delete(Long id) {
        Cliente cliente = repository.getActiveClientById(id);
        cliente.setEstado(false);
        repository.save(cliente);
        cuentaRestClient.notifyDelete(id);
    }

    @Override
    public List<ClienteResponse> getAll() {
        return repository.getAllActiveClients()
                .stream().map(ClienteMapper.INSTANCE::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ClienteResponse getById(Long id) {
        return ClienteMapper.INSTANCE
                .toResponse(repository.getActiveClientById(id));
    }

}
