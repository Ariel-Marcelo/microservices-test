package com.demo.trcuentas.infrastructure.repositories.movimiento;

import com.demo.trcuentas.domain.dtos.movimiento.MovimientoRepositoryPort;
import com.demo.trcuentas.domain.models.Movimiento;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MovimientoRepository implements MovimientoRepositoryPort {

    private final MovimientoJpaRepository jpaRepository;

    @Override
    public Movimiento save(Movimiento movimiento) {
        return jpaRepository.save(movimiento);
    }

    @Override
    public List<Movimiento> getAllMovimientos() {
        return jpaRepository.findAll();
    }

    @Override
    public Movimiento getMovimientosById(Long id) {
        return jpaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movimiento no encontrado con el ID: " + id));
    }

    @Override
    public List<Movimiento> getMovimientosByCuentaAndFechaOrderDesc(Long cuentaId, LocalDateTime inicio, LocalDateTime fin) {
        return jpaRepository.findByCuentaIdAndFechaBetweenOrderByFechaDesc(cuentaId, inicio, fin);
    }

    @Override
    public Optional<Movimiento> findLastByCuentaId(Long cuentaId) {
        return jpaRepository.findTopByCuenta_IdOrderByIdDesc(cuentaId);
    }
}
