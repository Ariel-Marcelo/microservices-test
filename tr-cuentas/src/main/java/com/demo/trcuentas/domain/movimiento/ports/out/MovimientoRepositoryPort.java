package com.demo.trcuentas.domain.movimiento.ports.out;

import com.demo.trcuentas.domain.movimiento.MovimientoDomain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovimientoRepositoryPort {
    MovimientoDomain save(MovimientoDomain movimiento);
    List<MovimientoDomain> getAllMovimientos();
    MovimientoDomain getMovimientosById(Long id);
    List<MovimientoDomain> getMovimientosByCuentaAndFechaOrderDesc(Long cuentaId, LocalDateTime inicio, LocalDateTime fin);
    Optional<MovimientoDomain> findLastByCuentaId(Long cuentaId);
}
