package com.demo.trcuentas.infrastructure.adapters.out.persistence.cuenta;

import com.demo.trcuentas.domain.cuenta.CuentaDomain;
import com.demo.trcuentas.domain.cuenta.CuentaMapper;
import com.demo.trcuentas.domain.cuenta.ports.out.CuentaRepositoryPort;
import com.demo.trcuentas.infrastructure.adapters.out.persistence.cliente.ClienteCuentaJpaRepository;
import com.demo.trcuentas.infrastructure.adapters.out.persistence.models.ClienteCuenta;
import com.demo.trcuentas.infrastructure.adapters.out.persistence.models.Cuenta;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class CuentaRepositoryAdapter implements CuentaRepositoryPort {

    private final CuentaJpaRepository jpaRepository;
    private final ClienteCuentaJpaRepository clienteRepository;
    private final CuentaMapper cuentaMapper;

    @Override
    public CuentaDomain save(CuentaDomain domain) {
        Cuenta entity = jpaRepository.findById(domain.getId() != null ? domain.getId() : -1L)
                .orElse(cuentaMapper.toEntity(domain));
        
        if (domain.getId() != null) {
            entity.setNumeroCuenta(domain.getNumeroCuenta());
            entity.setTipoCuenta(domain.getTipoCuenta());
            entity.setSaldoInicial(domain.getSaldoInicial());
            entity.setEstado(domain.getEstado());
        }

        if (domain.getClienteId() != null) {
            ClienteCuenta cliente = clienteRepository.findById(domain.getClienteId())
                    .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + domain.getClienteId()));
            entity.setCliente(cliente);
        }

        return cuentaMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<CuentaDomain> getAllActiveCuentas() {
        return jpaRepository.findByEstadoTrue().stream()
                .map(cuentaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public CuentaDomain getActiveCuentasById(Long id) {
        return jpaRepository.findByIdAndEstadoTrue(id)
                .map(cuentaMapper::toDomain)
                .orElseThrow(() -> new EntityNotFoundException("Cuenta no encontrada o inactiva con ID: " + id));
    }

    @Override
    public CuentaDomain findActiveCuentasByNumeroId(String numeroCuenta) {
        return jpaRepository.findByNumeroCuentaAndEstadoTrue(numeroCuenta)
                .map(cuentaMapper::toDomain)
                .orElseThrow(() -> new EntityNotFoundException("Cuenta no encontrada o inactiva con el número: " + numeroCuenta));
    }

    @Override
    public List<CuentaDomain> getCuentasByCliente(String clienteId) {
        return jpaRepository.findByCliente_ClienteId(clienteId).stream()
                .map(cuentaMapper::toDomain)
                .collect(Collectors.toList());
    }
}
