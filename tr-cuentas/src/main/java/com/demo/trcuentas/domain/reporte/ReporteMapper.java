package com.demo.trcuentas.domain.reporte;

import com.demo.trcuentas.infrastructure.adapters.out.persistence.models.Cuenta;
import com.demo.trcuentas.infrastructure.adapters.out.persistence.models.Movimiento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface ReporteMapper {

    ReporteMapper INSTANCE = Mappers.getMapper(ReporteMapper.class);

    MovimientoReporte toMovimientoDomain(Movimiento movimiento);

    @Mapping(source = "saldoInicial", target = "saldoActual")
    @Mapping(target = "movimientos", ignore = true)
    CuentaReporte toCuentaDomain(Cuenta cuenta);

    default OffsetDateTime map(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        return value.atOffset(ZoneOffset.UTC);
    }
}
