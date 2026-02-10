package com.demo.trcuentas.domain.movimiento.ports.in;

import com.demo.trcuentas.domain.movimiento.MovimientoDomain;

import java.util.List;

public interface MovimientoServicePort {
    MovimientoDomain create(MovimientoDomain domain);
    List<MovimientoDomain> getAll();
    MovimientoDomain getById(Long id);
    void delete(Long id);
    MovimientoDomain update(Long id, MovimientoDomain domain);
}
