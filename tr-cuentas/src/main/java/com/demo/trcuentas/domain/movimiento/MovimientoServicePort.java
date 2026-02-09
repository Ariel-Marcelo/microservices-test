package com.demo.trcuentas.domain.movimiento;

import java.util.List;

public interface MovimientoServicePort {
    MovimientoDomain create(MovimientoRequestDomain request);
    List<MovimientoDomain> getAll();
    MovimientoDomain getById(Long id);
    void delete(Long id);
    MovimientoDomain update(Long id, MovimientoRequestDomain request);
}