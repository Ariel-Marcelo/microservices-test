package com.demo.trclientes.controllers;

import com.demo.trclientes.domain.dtos.cliente.ClienteServicePort;
import com.demo.trclientes.infrastructure.adapters.in.rest.dtos.*;
import com.demo.trclientes.infrastructure.adapters.in.rest.api.ClientesApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClienteController implements ClientesApi {

    private final ClienteServicePort clientService;

    @Override
    public ResponseEntity<ApiResponseCliente> createClient(ClienteRequest clienteRequest) {
        log.info("INICIO PETICIÓN: [POST /api/v1/clients] - Solicitud de creación de Cliente. Identificación: {}", clienteRequest.getIdentificacion());
        ClienteResponse createdClient = clientService.create(clienteRequest);
        
        log.info("FIN PETICIÓN: [POST /api/v1/clients] - Cliente ID {} creado exitosamente. Status: 201 Created.", createdClient.getId());
        
        ApiResponseCliente response = new ApiResponseCliente()
                .status(true)
                .data(createdClient);
                
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<ApiResponseListCliente> getAllClients() {
        log.info("INICIO PETICIÓN: [GET /api/v1/clients] - Solicitud de listado de todos los clientes.");
        List<ClienteResponse> clients = clientService.getAll();
        
        log.info("FIN PETICIÓN: [GET /api/v1/clients] - Listado de {} clientes devuelto. Status: 200 OK.", clients.size());
        
        ApiResponseListCliente response = new ApiResponseListCliente()
                .status(true)
                .data(clients);
                
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponseCliente> getClientById(Long id) {
        log.info("INICIO PETICIÓN: [GET /api/v1/clients/{}] - Búsqueda de cliente por ID.", id);
        ClienteResponse client = clientService.getById(id);
        
        log.info("FIN PETICIÓN: [GET /api/v1/clients/{}] - Cliente ID {} encontrado. Status: 200 OK.", id, id);
        
        ApiResponseCliente response = new ApiResponseCliente()
                .status(true)
                .data(client);
                
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponseCliente> updateClient(Long id, ClienteRequest clienteRequest) {
        log.info("INICIO PETICIÓN: [PUT /api/v1/clients/{}] - Solicitud de actualización de Cliente ID: {}. Identificación: {}.", id, id, clienteRequest.getIdentificacion());
        ClienteResponse updatedClient = clientService.update(id, clienteRequest);
        
        log.info("FIN PETICIÓN: [PUT /api/v1/clients/{}] - Cliente ID {} actualizado. Status: 200 OK.", id, id);
        
        ApiResponseCliente response = new ApiResponseCliente()
                .status(true)
                .data(updatedClient);
                
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponseVoid> deleteClient(Long id) {
        log.warn("INICIO PETICIÓN: [DELETE /api/v1/clients/{}] - Solicitud de ELIMINACIÓN LÓGICA de Cliente ID {}.", id, id);
        clientService.delete(id);
        
        log.warn("FIN PETICIÓN: [DELETE /api/v1/clients/{}] - Cliente ID {} marcado como inactivo. Status: 200 OK.", id, id);
        
        ApiResponseVoid response = new ApiResponseVoid()
                .status(true)
                .data(null);
                
        return ResponseEntity.ok(response);
    }
}
