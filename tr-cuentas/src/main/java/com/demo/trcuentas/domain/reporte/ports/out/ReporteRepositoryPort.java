package com.demo.trcuentas.domain.reporte.ports.out;

import com.demo.trcuentas.domain.reporte.EstadoCuentaReporte;
import com.demo.trcuentas.domain.reporte.ReporteConsulta;

import java.util.Optional;

public interface ReporteRepositoryPort {
    Optional<EstadoCuentaReporte> obtenerEstadoCuentaCompleto(ReporteConsulta consulta);
}
