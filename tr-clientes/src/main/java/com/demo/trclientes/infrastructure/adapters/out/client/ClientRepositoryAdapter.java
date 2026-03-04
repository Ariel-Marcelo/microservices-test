package com.demo.trclientes.infrastructure.adapters.out.client;

import com.demo.trclientes.domain.client.ports.out.ClientRepositoryPort;
import com.demo.trclientes.domain.client.models.Client;
import com.demo.trclientes.infrastructure.shared.mappers.ClientMapper;
import com.demo.trclientes.infrastructure.persistence.models.ClientEntity;
import com.demo.trclientes.domain.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ClientRepositoryAdapter implements ClientRepositoryPort {

    private final ClientJpaRepository jpaRepository;
    private final ClientMapper mapper;

    @Override
    public Client save(Client clientDomain) {
        ClientEntity entity = mapper.toEntity(clientDomain);
        ClientEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public List<Client> getAllActiveClients() {
        return jpaRepository.findByStateTrue().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Client getActiveClientById(Long id) {
        ClientEntity entity = jpaRepository.findByIdAndStateTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado o inactivo con ID: " + id));
        return mapper.toDomain(entity);
    }
}
