package com.demo.trcuentas.domain.dtos.reporte.response;

import com.demo.trcuentas.domain.dtos.reporte.response.ReporteCuenta;
import lombok.Data;

import java.util.List;

@Data
public class ReporteEstadoCuentaResponse {
    private String clienteId;
    private String nombreCliente;
    private String rangoFechasSolicitado;
    private List<ReporteCuenta> cuentas;
}
