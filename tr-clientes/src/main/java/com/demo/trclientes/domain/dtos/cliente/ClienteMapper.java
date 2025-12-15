package com.demo.trclientes.domain.dtos.cliente;

import com.demo.trclientes.domain.dtos.cliente.requests.ClienteRequest;
import com.demo.trclientes.domain.dtos.cliente.responses.ClienteResponse;
import com.demo.trclientes.domain.models.Cliente;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteMapper INSTANCE = Mappers.getMapper(ClienteMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", defaultValue = "true")
    @Mapping(source = "clienteId", target = "clienteId")
    Cliente toEntity(ClienteRequest request);

    ClienteResponse toResponse(Cliente cliente);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(ClienteRequest request, @MappingTarget Cliente entity);
}