package com.demo.trcuentas.infrastructure.adapters.in.mappers;

import com.demo.trcuentas.domain.clienteCuenta.ClienteDomain;
import com.demo.trcuentas.domain.cuenta.CuentaDomain;
import com.demo.trcuentas.domain.movimiento.MovimientoDomain;
import com.demo.trcuentas.domain.reporte.EstadoCuentaReporte;
import com.demo.trcuentas.domain.reporte.ReporteConsulta;
import com.demo.trcuentas.infrastructure.adapters.in.rest.dtos.*;
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
    CuentaDomain toDomain(CuentaRequest request);
    CuentaResponse toRest(CuentaDomain domain);

    // Movimiento mappings
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fecha", ignore = true)
    @Mapping(target = "saldo", ignore = true)
    @Mapping(target = "cuentaId", ignore = true)
    MovimientoDomain toDomain(MovimientoRequest request);
    MovimientoResponse toRest(MovimientoDomain domain);

    // Cliente mappings
    ClienteDomain toDomain(ClienteRequest request);

    // Report mappings
    default ReporteConsulta toDomain(String clienteId, LocalDate startDate, LocalDate endDate) {
        return ReporteConsulta.builder()
                .clienteId(clienteId)
                .fechaInicio(startDate)
                .fechaFin(endDate)
                .build();
    }

    ReporteEstadoCuentaResponse toRest(EstadoCuentaReporte response);


    default OffsetDateTime map(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        return value.atOffset(ZoneOffset.UTC);
    }
}
