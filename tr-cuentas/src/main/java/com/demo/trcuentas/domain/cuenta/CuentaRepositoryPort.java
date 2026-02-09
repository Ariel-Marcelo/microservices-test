package com.demo.trcuentas.domain.cuenta;

import java.util.List;

public interface CuentaRepositoryPort {
    CuentaDomain save(CuentaDomain cuenta);
    List<CuentaDomain> getAllActiveCuentas();
    CuentaDomain getActiveCuentasById(Long id);
    CuentaDomain findActiveCuentasByNumeroId(String numeroCuenta);
    List<CuentaDomain> getCuentasByCliente(String clienteId);
}