package com.demo.trcuentas.domain.clienteCuenta;

import com.demo.trcuentas.infrastructure.adapters.out.persistence.models.ClienteCuenta;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ClienteMapper {
    ClienteMapper INSTANCE = Mappers.getMapper(ClienteMapper.class);

    ClienteDomain toDomain(ClienteCuenta entity);
    ClienteCuenta toEntity(ClienteDomain domain);

    void updateEntityFromDomain(ClienteDomain domain, @MappingTarget ClienteCuenta entity);
}
