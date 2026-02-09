package com.demo.trcuentas.infrastructure.adapters.out.persistence.cliente;

import com.demo.trcuentas.domain.clienteCuenta.ClienteDomain;
import com.demo.trcuentas.domain.clienteCuenta.ClienteMapper;
import com.demo.trcuentas.domain.clienteCuenta.ClienteReplicaRepositoryPort;
import com.demo.trcuentas.infrastructure.adapters.out.persistence.models.ClienteCuenta;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ClienteReplicaRepositoryAdapter implements ClienteReplicaRepositoryPort {

    private final ClienteCuentaJpaRepository jpaRepository;
    private final ClienteMapper clienteMapper;

    @Override
    public Optional<ClienteDomain> findById(Long id) {
        return jpaRepository.findById(id).map(clienteMapper::toDomain);
    }

    @Override
    public Optional<ClienteDomain> findByIdAndEstadoTrue(Long id) {
        return jpaRepository.findByIdAndEstadoTrue(id).map(clienteMapper::toDomain);
    }

    @Override
    public Optional<ClienteDomain> findByClienteId(String clienteId) {
        return jpaRepository.findByClienteId(clienteId).map(clienteMapper::toDomain);
    }

    @Override
    public ClienteDomain save(ClienteDomain domain) {
        ClienteCuenta entity = jpaRepository.findById(domain.getId())
                .orElse(new ClienteCuenta());
        
        clienteMapper.updateEntityFromDomain(domain, entity);
        if (entity.getId() == null) {
            entity.setId(domain.getId());
        }
        
        return clienteMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}