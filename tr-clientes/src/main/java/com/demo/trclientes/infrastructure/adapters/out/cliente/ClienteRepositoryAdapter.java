package com.demo.trclientes.infrastructure.adapters.out.cliente;

import com.demo.trclientes.domain.cliente.ports.out.ClienteRepositoryPort;
import com.demo.trclientes.domain.cliente.models.Client;
import com.demo.trclientes.infrastructure.shared.mappers.ClientMapper;
import com.demo.trclientes.infrastructure.persistence.models.Cliente;
import com.demo.trclientes.domain.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository jpaRepository;
    private final ClientMapper mapper;

    @Override
    public Client save(Client clientDomain) {
        Cliente entity = mapper.toEntity(clientDomain);
        Cliente savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public List<Client> getAllActiveClients() {
        return jpaRepository.findByEstadoTrue().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Client getActiveClientById(Long id) {
        Cliente entity = jpaRepository.findByIdAndEstadoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado o inactivo con ID: " + id));
        return mapper.toDomain(entity);
    }

    @Override
    public Client getActiveClientByUniqueId(String id) {
        Cliente entity = jpaRepository.findByClienteId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado o inactivo con ID: " + id));
        return mapper.toDomain(entity);
    }
}
