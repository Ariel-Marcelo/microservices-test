package com.demo.trcuentas.domain.dtos.movimiento;

import com.demo.trcuentas.domain.models.Movimiento;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovimientoRepositoryPort {

    Movimiento save(Movimiento movimiento);

    List<Movimiento> getAllMovimientos();

    Movimiento getMovimientosById(Long id);

    List<Movimiento> getMovimientosByCuentaAndFechaOrderDesc(Long cuentaId, LocalDateTime inicio, LocalDateTime fin);

    Optional<Movimiento> findLastByCuentaId(Long cuentaId);
}
