package com.demo.trcuentas.domain.movimiento;

import com.demo.trcuentas.domain.movimiento.requests.MovimientoRequest;
import com.demo.trcuentas.domain.movimiento.responses.MovimientoResponse;

import java.util.List;

public interface MovimientoServicePort {

    MovimientoResponse create(MovimientoRequest movimientoRequest);

    List<MovimientoResponse> getAll();

    MovimientoResponse getById(Long id);

    void delete(Long id);

    MovimientoResponse update(Long id, MovimientoRequest request);

}
