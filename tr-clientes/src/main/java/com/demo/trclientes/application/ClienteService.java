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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ClienteService implements ClienteServicePort {

    private final ClienteRepositoryPort repository;
    private final CuentaRestClient cuentaRestClient;

    @Override
    public ClienteResponse create(ClienteRequest clienteRequest) {
        log.info("INICIO CREATE CLIENTE: Creando cliente con Identificación: {}", clienteRequest.getIdentificacion());

        var user = ClienteMapper.INSTANCE.toEntity(clienteRequest);
        Cliente cliente = repository.save(user);

        log.info("CLIENTE GUARDADO: Cliente ID {} guardado en BD local. Preparando réplica...", cliente.getId());

        ClienteReplica replica = ClienteReplica.builder()
                .id(cliente.getId())
                .clienteId(cliente.getClienteId())
                .nombre(cliente.getNombre())
                .build();

        cuentaRestClient.notifyCreate(replica);

        log.info("FIN CREATE CLIENTE: Cliente ID {} creado y replicación exitosa. Transacción completada.", cliente.getId());
        return ClienteMapper.INSTANCE.toResponse(cliente);
    }

    @Override
    public ClienteResponse update(Long id, ClienteRequest clienteRequest) {
        log.warn("INICIO UPDATE CLIENTE: Actualizando Cliente ID {}. Identificación: {}", id, clienteRequest.getIdentificacion());

        Cliente client = repository.getActiveClientById(id);
        ClienteMapper.INSTANCE.updateEntityFromRequest(clienteRequest, client);
        Cliente updatedClient = repository.save(client);
        
        log.debug("CLIENTE ACTUALIZADO: Datos de Cliente ID {} guardados en BD local.", id);

        ClienteReplica clone = ClienteReplica.builder()
                .id(updatedClient.getId())
                .clienteId(updatedClient.getClienteId())
                .nombre(updatedClient.getNombre())
                .build();
        
        cuentaRestClient.notifyUpdate(updatedClient.getId(), clone);

        log.warn("FIN UPDATE CLIENTE: Cliente ID {} actualizado y réplica enviada. Transacción completada.", id);
        return ClienteMapper.INSTANCE.toResponse(updatedClient);
    }

    @Override
    public void delete(Long id) {
        log.error("INICIO DELETE CLIENTE: Solicitud de ELIMINACIÓN LÓGICA para Cliente ID {}.", id);

        Cliente client = repository.getActiveClientById(id);
        client.setEstado(false);
        repository.save(client);
        
        log.warn("CLIENTE INHABILITADO: Cliente ID {} marcado como INACTIVO en BD local.", id);

        cuentaRestClient.notifyDelete(id);

        log.error("FIN DELETE CLIENTE: Cliente ID {} inhabilitado y réplica enviada. Transacción completada.", id);
    }

    @Override
    public List<ClienteResponse> getAll() {
        log.info("INICIO GET ALL CLIENTES: Recuperando todos los clientes activos.");

        List<ClienteResponse> response = repository.getAllActiveClients()
                .stream().map(ClienteMapper.INSTANCE::toResponse)
                .collect(Collectors.toList());

        log.info("FIN GET ALL CLIENTES: {} clientes activos recuperados.", response.size());
        return response;
    }

    @Override
    public ClienteResponse getById(Long id) {
        log.info("INICIO GET BY ID CLIENTE: Buscando cliente activo por ID {}.", id);
        
        ClienteResponse response = ClienteMapper.INSTANCE
                .toResponse(repository.getActiveClientById(id));
        
        log.info("FIN GET BY ID CLIENTE: Cliente ID {} encontrado.", id);
        return response;
    }

}