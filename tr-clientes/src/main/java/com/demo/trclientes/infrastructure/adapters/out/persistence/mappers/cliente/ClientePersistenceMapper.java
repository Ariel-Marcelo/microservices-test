package com.demo.trclientes.infrastructure.adapters.out.persistence.mappers.cliente;

import com.demo.trclientes.domain.cliente.models.Client;
import com.demo.trclientes.infrastructure.adapters.out.persistence.models.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ClientePersistenceMapper {

    ClientePersistenceMapper INSTANCE = Mappers.getMapper(ClientePersistenceMapper.class);

    Cliente toEntity(Client client);

    Client toDomain(Cliente entity);
}
