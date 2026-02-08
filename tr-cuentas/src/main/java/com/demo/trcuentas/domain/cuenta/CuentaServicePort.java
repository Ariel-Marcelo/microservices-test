package com.demo.trcuentas.domain.cuenta;

import com.demo.trcuentas.domain.cuenta.responses.CuentaResponse;
import com.demo.trcuentas.domain.cuenta.requests.CuentaRequest;

import java.util.List;

public interface CuentaServicePort {
    CuentaResponse create(CuentaRequest cuentaRequest);

    List<CuentaResponse> getAll();

    CuentaResponse getById(Long id);

    CuentaResponse update(Long id, CuentaRequest cuentaRequest);

    void delete(Long id);
}
