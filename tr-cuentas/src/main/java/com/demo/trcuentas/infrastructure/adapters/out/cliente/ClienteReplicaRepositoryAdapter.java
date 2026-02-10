package com.demo.trcuentas.infrastructure.adapters.out.cliente;

import com.demo.trcuentas.domain.clienteCuenta.ClienteDomain;
import com.demo.trcuentas.domain.clienteCuenta.ClienteMapper;
import com.demo.trcuentas.domain.clienteCuenta.ports.out.ClienteReplicaRepositoryPort;
import com.demo.trcuentas.infrastructure.persistence.models.ClienteCuenta;

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
        ClienteCuenta entity = (domain.getId() != null)
                ? jpaRepository.findById(domain.getId()).orElse(new ClienteCuenta())
                : new ClienteCuenta();
        
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
