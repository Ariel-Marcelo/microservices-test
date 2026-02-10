package com.demo.trcuentas.domain.cuenta;

import com.demo.trcuentas.infrastructure.adapters.out.persistence.models.Cuenta;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CuentaMapper {

    CuentaMapper INSTANCE = Mappers.getMapper(CuentaMapper.class);

    @Mapping(target = "movimientos", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    Cuenta toEntity(CuentaDomain domain);

    @Mapping(target = "clienteId", source = "cliente.id")
    @Mapping(target = "nombreCliente", source = "cliente.nombre")
    CuentaDomain toDomain(Cuenta cuenta);
}
