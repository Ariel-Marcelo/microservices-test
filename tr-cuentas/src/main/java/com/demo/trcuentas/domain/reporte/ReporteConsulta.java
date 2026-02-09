package com.demo.trcuentas.domain.reporte;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class ReporteConsulta {
    private String clienteId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
