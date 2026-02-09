package com.demo.trcuentas.domain.cuenta;

import java.util.List;

public interface CuentaServicePort {
    CuentaDomain create(CuentaRequestDomain request);
    List<CuentaDomain> getAll();
    CuentaDomain getById(Long id);
    CuentaDomain update(Long id, CuentaRequestDomain request);
    void delete(Long id);
}