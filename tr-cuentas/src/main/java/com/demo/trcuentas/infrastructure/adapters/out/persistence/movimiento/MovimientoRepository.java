package com.demo.trcuentas.infrastructure.adapters.out.persistence.movimiento;

import com.demo.trcuentas.domain.movimiento.MovimientoDomain;
import com.demo.trcuentas.domain.movimiento.MovimientoMapper;
import com.demo.trcuentas.domain.movimiento.MovimientoRepositoryPort;
import com.demo.trcuentas.infrastructure.adapters.out.persistence.cuenta.CuentaJpaRepository;
import com.demo.trcuentas.infrastructure.adapters.out.persistence.models.Cuenta;
import com.demo.trcuentas.infrastructure.adapters.out.persistence.models.Movimiento;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MovimientoRepository implements MovimientoRepositoryPort {

    private final MovimientoJpaRepository jpaRepository;
    private final CuentaJpaRepository cuentaRepository;
    private final MovimientoMapper movimientoMapper;

    @Override
    public MovimientoDomain save(MovimientoDomain domain) {
        Movimiento entity = movimientoMapper.toEntity(domain);
        
        if (domain.getCuentaId() != null) {
            Cuenta cuenta = cuentaRepository.findById(domain.getCuentaId())
                    .orElseThrow(() -> new EntityNotFoundException("Cuenta no encontrada con ID: " + domain.getCuentaId()));
            entity.setCuenta(cuenta);
        }
        
        return movimientoMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<MovimientoDomain> getAllMovimientos() {
        return jpaRepository.findAll().stream()
                .map(movimientoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public MovimientoDomain getMovimientosById(Long id) {
        return jpaRepository.findById(id)
                .map(movimientoMapper::toDomain)
                .orElseThrow(() -> new EntityNotFoundException("Movimiento no encontrado con el ID: " + id));
    }

    @Override
    public List<MovimientoDomain> getMovimientosByCuentaAndFechaOrderDesc(Long cuentaId, LocalDateTime inicio, LocalDateTime fin) {
        return jpaRepository.findByCuentaIdAndFechaBetweenOrderByFechaDesc(cuentaId, inicio, fin).stream()
                .map(movimientoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MovimientoDomain> findLastByCuentaId(Long cuentaId) {
        return jpaRepository.findTopByCuenta_IdOrderByIdDesc(cuentaId)
                .map(movimientoMapper::toDomain);
    }
}
