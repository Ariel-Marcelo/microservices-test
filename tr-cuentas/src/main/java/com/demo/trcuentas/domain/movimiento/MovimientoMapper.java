package com.demo.trcuentas.domain.movimiento;

import com.demo.trcuentas.domain.dtos.MovimientoRequest;
import com.demo.trcuentas.domain.dtos.MovimientoResponse;
import com.demo.trcuentas.infrastructure.adapters.out.persistence.models.Movimiento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface MovimientoMapper {

    MovimientoMapper INSTANCE = Mappers.getMapper(MovimientoMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cuenta", ignore = true)
    @Mapping(target = "saldo", ignore = true)
    @Mapping(target = "fecha", expression = "java(LocalDateTime.now())")
    Movimiento toEntity(MovimientoRequest request);

    @Mapping(source = "cuenta.numeroCuenta", target = "numeroCuenta")
    @Mapping(source = "cuenta.tipoCuenta", target = "tipoCuenta")
    @Mapping(source = "cuenta.estado", target = "estado")
    MovimientoResponse toResponse(Movimiento movimiento);

    default OffsetDateTime map(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        return value.atOffset(ZoneOffset.UTC);
    }
}