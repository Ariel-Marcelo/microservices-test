package com.demo.trcuentas.domain.movimiento;

import com.demo.trcuentas.infrastructure.persistence.models.Movimiento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface MovimientoMapper {

    MovimientoMapper INSTANCE = Mappers.getMapper(MovimientoMapper.class);

    @Mapping(target = "cuenta", ignore = true)
    Movimiento toEntity(MovimientoDomain domain);

    @Mapping(target = "cuentaId", source = "cuenta.id")
    MovimientoDomain toDomain(Movimiento entity);

    default OffsetDateTime map(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        return value.atOffset(ZoneOffset.UTC);
    }
}
