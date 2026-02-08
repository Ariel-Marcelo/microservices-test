package com.demo.trclientes.infrastructure.adapters.in.mappers.cliente;

import com.demo.trclientes.domain.cliente.models.Client;
import com.demo.trclientes.infrastructure.adapters.in.rest.dtos.ClienteRequest;
import com.demo.trclientes.infrastructure.adapters.in.rest.dtos.ClienteResponse;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ClienteWebMapper {

    ClienteWebMapper INSTANCE = Mappers.getMapper(ClienteWebMapper.class);

    @Mapping(target = "id", ignore = true)
    Client toDomain(ClienteRequest request);

    ClienteResponse toResponse(Client client);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateDomainFromRequest(ClienteRequest request, @MappingTarget Client domain);
}
