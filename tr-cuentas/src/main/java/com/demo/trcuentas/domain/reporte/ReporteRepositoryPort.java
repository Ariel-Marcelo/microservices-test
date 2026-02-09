package com.demo.trcuentas.domain.reporte;

import java.util.Optional;

public interface ReporteRepositoryPort {
    Optional<EstadoCuentaReporte> obtenerEstadoCuentaCompleto(ReporteConsulta consulta);
}
