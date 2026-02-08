package com.demo.trcuentas.domain.cuenta;

import com.demo.trcuentas.domain.cuenta.requests.CuentaRequest;
import com.demo.trcuentas.domain.cuenta.responses.CuentaResponse;
import com.demo.trcuentas.infrastructure.adapters.out.models.Cuenta;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CuentaMapper {

    CuentaMapper INSTANCE = Mappers.getMapper(CuentaMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "movimientos", ignore = true)
    @Mapping(target = "estado", defaultValue = "true")
    @Mapping(target = "cliente", ignore = true)
    Cuenta toEntity(CuentaRequest request);

    CuentaResponse toResponse(Cuenta cuenta);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "movimientos", ignore = true)
    void updateEntityFromRequest(CuentaRequest request, @MappingTarget Cuenta entity);

}