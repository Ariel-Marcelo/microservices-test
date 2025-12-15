package com.demo.trcuentas.domain.dtos.movimiento;

import com.demo.trcuentas.domain.dtos.movimiento.requests.MovimientoRequest;
import com.demo.trcuentas.domain.dtos.movimiento.responses.MovimientoResponse;
import com.demo.trcuentas.domain.models.Movimiento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

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
}