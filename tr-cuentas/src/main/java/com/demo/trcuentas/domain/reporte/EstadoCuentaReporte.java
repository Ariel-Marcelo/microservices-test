package com.demo.trcuentas.domain.reporte;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class EstadoCuentaReporte {
    private String clienteId;
    private String nombreCliente;
    private String rangoFechasSolicitado;
    private List<CuentaReporte> cuentas;
}
