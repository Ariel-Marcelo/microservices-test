package com.demo.trcuentas.infrastructure.repositories.movimiento;

import com.demo.trcuentas.domain.models.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovimientoJpaRepository extends JpaRepository<Movimiento, Long> {
    List<Movimiento> findByCuentaIdAndFechaBetweenOrderByFechaDesc(Long cuentaId, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    Optional<Movimiento> findTopByCuenta_IdOrderByIdDesc(Long cuentaId);
}
