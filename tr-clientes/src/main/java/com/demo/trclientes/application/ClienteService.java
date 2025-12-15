package com.demo.trclientes.application;

import com.demo.trclientes.config.RabbitConfig;
import com.demo.trclientes.domain.dtos.cliente.ClienteMapper;
import com.demo.trclientes.domain.dtos.cliente.ClienteRepositoryPort;
import com.demo.trclientes.domain.dtos.cliente.ClienteServicePort;
import com.demo.trclientes.domain.dtos.cliente.events.ClienteCreatedEvent;
import com.demo.trclientes.domain.dtos.cliente.events.ClienteDeletedEvent;
import com.demo.trclientes.domain.dtos.cliente.events.ClienteUpdatedEvent;
import com.demo.trclientes.domain.dtos.cliente.requests.ClienteRequest;
import com.demo.trclientes.domain.dtos.cliente.responses.ClienteResponse;
import com.demo.trclientes.domain.models.Cliente;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteService implements ClienteServicePort {

    private final ClienteRepositoryPort repository;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public ClienteResponse create(ClienteRequest clienteRequest) {
        var user = ClienteMapper.INSTANCE.toEntity(clienteRequest);
        Cliente cliente = repository.save(user);
        ClienteCreatedEvent event = new ClienteCreatedEvent(
                cliente.getId(),
                cliente.getClienteId(),
                cliente.getNombre()
        );
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, RabbitConfig.ROUTING_KEY_CREATED, event);
        return ClienteMapper.INSTANCE.toResponse(cliente);
    }

    @Override
    public ClienteResponse update(Long id, ClienteRequest clienteRequest) {
        Cliente client = repository.getActiveClientById(id);
        ClienteMapper.INSTANCE.updateEntityFromRequest(clienteRequest, client);
        Cliente updatedClient = repository.save(client);

        ClienteUpdatedEvent event = new ClienteUpdatedEvent(
                updatedClient.getId(),
                updatedClient.getClienteId(),
                updatedClient.getNombre()
        );
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTING_KEY_UPDATED,
                event
        );
        return ClienteMapper.INSTANCE.toResponse(updatedClient);
    }

    @Override
    public void delete(Long id) {
        Cliente cliente = repository.getActiveClientById(id);
        cliente.setEstado(false);
        repository.save(cliente);
        ClienteDeletedEvent event = new ClienteDeletedEvent(id);
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTING_KEY_DELETED,
                event
        );
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
