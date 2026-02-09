package com.demo.trcuentas.domain.cuenta;

import java.util.List;

public interface CuentaServicePort {
    CuentaDomain create(CuentaDomain domain);
    List<CuentaDomain> getAll();
    CuentaDomain getById(Long id);
    CuentaDomain update(Long id, CuentaDomain domain);
    void delete(Long id);
}
