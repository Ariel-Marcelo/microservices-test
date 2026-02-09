package com.demo.trcuentas.infrastructure.adapters.in.mappers;

import com.demo.trcuentas.domain.dtos.CuentaRequest;
import com.demo.trcuentas.domain.dtos.CuentaResponse;
import com.demo.trcuentas.domain.dtos.MovimientoRequest;
import com.demo.trcuentas.domain.dtos.MovimientoResponse;
import com.demo.trcuentas.domain.dtos.ClienteRequest;
import com.demo.trcuentas.domain.dtos.ReporteEstadoCuentaResponse;
import com.demo.trcuentas.domain.dtos.ReporteCuenta;
import com.demo.trcuentas.domain.dtos.ReporteMovimiento;
import com.demo.trcuentas.domain.reporte.CuentaReporte;
import com.demo.trcuentas.domain.reporte.EstadoCuentaReporte;
import com.demo.trcuentas.domain.reporte.MovimientoReporte;
import com.demo.trcuentas.domain.reporte.ReporteConsulta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface RestMapper {

    RestMapper INSTANCE = Mappers.getMapper(RestMapper.class);

    // Cuenta mappings
    default CuentaRequest toDomain(CuentaRequest request) { return request; }
    default CuentaResponse toRest(CuentaResponse response) { return response; }

    // Movimiento mappings
    default MovimientoRequest toDomain(MovimientoRequest request) { return request; }
    default MovimientoResponse toRest(MovimientoResponse response) { return response; }

    // Cliente mappings
    default ClienteRequest toDomain(ClienteRequest request) { return request; }

    // Report mappings
    default ReporteConsulta toDomain(String clienteId, LocalDate startDate, LocalDate endDate) {
        return ReporteConsulta.builder()
                .clienteId(clienteId)
                .fechaInicio(startDate)
                .fechaFin(endDate)
                .build();
    }

    ReporteEstadoCuentaResponse toRest(EstadoCuentaReporte response);

    @Mapping(target = "movimientos", source = "movimientos")
    ReporteCuenta toRest(CuentaReporte response);

    ReporteMovimiento toRest(MovimientoReporte response);

    default OffsetDateTime map(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        return value.atOffset(ZoneOffset.UTC);
    }
}
