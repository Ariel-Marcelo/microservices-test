package com.demo.trclientes.infrastructure.adapters.out.external.cliente.mappers;

import com.demo.trclientes.domain.cliente.models.Client;
import com.demo.trclientes.infrastructure.adapters.out.external.dtos.ClienteReplica;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ClienteExternalMapper {
    ClienteExternalMapper INSTANCE = Mappers.getMapper(ClienteExternalMapper.class);

    ClienteReplica toReplica(Client client);
}
