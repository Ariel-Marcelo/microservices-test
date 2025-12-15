package com.demo.trcuentas.domain.dtos.cuenta;

import com.demo.trcuentas.domain.dtos.cuenta.requests.CuentaRequest;
import com.demo.trcuentas.domain.dtos.cuenta.responses.CuentaResponse;

import java.util.List;

public interface CuentaServicePort {
    CuentaResponse create(CuentaRequest cuentaRequest);

    List<CuentaResponse> getAll();

    CuentaResponse getById(Long id);

    CuentaResponse update(Long id, CuentaRequest cuentaRequest);

    void delete(Long id);
}
