package com.demo.trclientes.infrastructure.adapters.in.controllers.cliente;

import com.demo.trclientes.domain.cliente.ports.in.ClienteCommandServicePort;
import com.demo.trclientes.domain.cliente.ports.in.ClienteQueryServicePort;
import com.demo.trclientes.domain.cliente.models.Client;
import com.demo.trclientes.infrastructure.shared.mappers.ClientMapper;
import com.demo.trclientes.infrastructure.adapters.in.rest.dtos.*;
import com.demo.trclientes.infrastructure.adapters.in.rest.api.ClientesApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ClienteController implements ClientesApi {

    private final ClienteCommandServicePort commandService;
    private final ClienteQueryServicePort queryService;
    private final ClientMapper mapper;

    @Override
    public ResponseEntity<ApiResponseCliente> createClient(ClienteRequest clienteRequest) {
        log.info("INICIO PETICIÓN: [POST /api/v1/clients] - Solicitud de creación de Cliente. Identificación: {}", clienteRequest.getIdentificacion());
        
        Client clientDomain = mapper.toDomain(clienteRequest);
        Client createdClient = commandService.create(clientDomain);
        ClienteResponse responseDto = mapper.toResponse(createdClient);
        
        log.info("FIN PETICIÓN: [POST /api/v1/clients] - Cliente ID {} creado exitosamente. Status: 201 Created.", responseDto.getId());
        ApiResponseCliente response = new ApiResponseCliente()
                .status(true)
                .data(responseDto);
                
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<ApiResponseListCliente> getAllClients() {
        log.info("INICIO PETICIÓN: [GET /api/v1/clients] - Solicitud de listado de todos los clientes.");
        
        List<ClienteResponse> clients = queryService.getAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        
        log.info("FIN PETICIÓN: [GET /api/v1/clients] - Listado de {} clientes devuelto. Status: 200 OK.", clients.size());
        
        ApiResponseListCliente response = new ApiResponseListCliente()
                .status(true)
                .data(clients);
                
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponseCliente> getClientById(Long id) {
        log.info("INICIO PETICIÓN: [GET /api/v1/clients/{}] - Búsqueda de cliente por ID.", id);

        Client clientDomain = queryService.getById(id);
        ClienteResponse responseDto = mapper.toResponse(clientDomain);
        
        log.info("FIN PETICIÓN: [GET /api/v1/clients/{}] - Cliente ID {} encontrado. Status: 200 OK.", id, id);

        ApiResponseCliente response = new ApiResponseCliente()
                .status(true)
                .data(responseDto);
                
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponseCliente> updateClient(Long id, ClienteRequest clienteRequest) {

        Client clientToUpdate = mapper.toDomain(clienteRequest);
        Client updatedClient = commandService.update(id, clientToUpdate);
        ClienteResponse responseDto = mapper.toResponse(updatedClient);


        ApiResponseCliente response = new ApiResponseCliente()
                .status(true)
                .data(responseDto);
                
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponseVoid> deleteClient(Long id) {
        log.info("INICIO PETICIÓN: [DELETE /api/v1/clients/{}] - Solicitud de ELIMINACIÓN LÓGICA de Cliente ID {}.", id, id);
        commandService.delete(id);
        
        log.info("FIN PETICIÓN: [DELETE /api/v1/clients/{}] - Cliente ID {} marcado como inactivo. Status: 200 OK.", id, id);

        ApiResponseVoid response = new ApiResponseVoid()
                .status(true)
                .data(null);
                
        return ResponseEntity.ok(response);
    }
}
