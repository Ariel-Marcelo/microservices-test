package com.demo.trclientes.infrastructure.shared.mappers;

import com.demo.trclientes.domain.cliente.models.Client;
import com.demo.trclientes.infrastructure.adapters.in.rest.dtos.ClienteRequest;
import com.demo.trclientes.infrastructure.adapters.in.rest.dtos.ClienteResponse;
import com.demo.trclientes.infrastructure.persistence.models.Cliente;
import com.demo.trclientes.infrastructure.shared.dtos.ClienteReplica;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    Cliente toEntity(Client client);

    Client toDomain(Cliente entity);

    @Mapping(target = "id", ignore = true)
    Client toDomain(ClienteRequest request);

    ClienteResponse toResponse(Client client);

    ClienteReplica toReplica(Client client);
}
