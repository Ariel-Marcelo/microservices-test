package com.demo.trclientes.infrastructure.infrastructure.cliente;

import com.demo.trclientes.domain.dtos.cliente.ClienteRepositoryPort;
import com.demo.trclientes.domain.models.Cliente;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository jpaRepository;

    @Override
    public Cliente save(Cliente cliente) {
        return jpaRepository.save(cliente);
    }

    @Override
    public List<Cliente> getAllActiveClients() {
        var list =  jpaRepository.findByEstadoTrue();
        return list;
    }

    @Override
    public Cliente getActiveClientById(Long id) {
        return jpaRepository.findByIdAndEstadoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado o inactivo con ID: " + id));
    }

    @Override
    public Cliente getActiveClientByUniqueId(String id) {
        return jpaRepository.findByClienteId(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado o inactivo con ID: " + id));
    }
}
