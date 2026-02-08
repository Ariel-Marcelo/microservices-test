package com.demo.trclientes.infrastructure.adapters.in.mappers.cliente;

import com.demo.trclientes.infrastructure.adapters.in.rest.dtos.ClienteRequest;
import com.demo.trclientes.infrastructure.adapters.in.rest.dtos.ClienteResponse;
import com.demo.trclientes.infrastructure.adapters.out.persistence.models.Cliente;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteMapper INSTANCE = Mappers.getMapper(ClienteMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", source = "estado", defaultValue = "true")
    @Mapping(source = "clienteId", target = "clienteId")
    Cliente toEntity(ClienteRequest request);

    ClienteResponse toResponse(Cliente cliente);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(ClienteRequest request, @MappingTarget Cliente entity);
}