package com.demo.trcuentas.domain.cuenta;

import com.demo.trcuentas.domain.dtos.CuentaRequest;
import com.demo.trcuentas.domain.dtos.CuentaResponse;
import com.demo.trcuentas.infrastructure.adapters.out.persistence.models.Cuenta;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")

public interface CuentaMapper {



    CuentaMapper INSTANCE = Mappers.getMapper(CuentaMapper.class);



        @Mapping(target = "id", ignore = true)



        @Mapping(target = "movimientos", ignore = true)



        @Mapping(target = "estado", defaultValue = "true")



        @Mapping(target = "cliente", ignore = true)



        Cuenta toEntity(CuentaRequestDomain request);



    



        @Mapping(target = "movimientos", ignore = true)



        @Mapping(target = "cliente", ignore = true)



        Cuenta toEntity(CuentaDomain domain);



    



        @Mapping(target = "clienteId", source = "cliente.id")



    

    @Mapping(target = "nombreCliente", source = "cliente.nombre")

    CuentaDomain toDomain(Cuenta cuenta);



    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

    @Mapping(target = "id", ignore = true)

    @Mapping(target = "movimientos", ignore = true)

    @Mapping(target = "cliente", ignore = true)

    void updateEntityFromRequest(CuentaRequestDomain request, @MappingTarget Cuenta entity);



}
