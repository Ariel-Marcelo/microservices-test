package com.demo.trclientes.controllers;

import com.demo.trclientes.domain.dtos.ApiResponse;
import com.demo.trclientes.domain.dtos.cliente.ClienteServicePort;
import com.demo.trclientes.domain.dtos.cliente.requests.ClienteRequest;
import com.demo.trclientes.domain.dtos.cliente.responses.ClienteResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteServicePort clientService;

    @PostMapping
    public ResponseEntity<ApiResponse<ClienteResponse>> createClient(@Valid @RequestBody ClienteRequest clienteRequest) {
        log.info("INICIO PETICIÓN: [POST /api/v1/clients] - Solicitud de creación de Cliente. Identificación: {}", clienteRequest.getIdentificacion());
        ClienteResponse createdClient = clientService.create(clienteRequest);
        
        log.info("FIN PETICIÓN: [POST /api/v1/clients] - Cliente ID {} creado exitosamente. Inicia proceso de replicación REST. Status: 201 Created.", createdClient.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdClient));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClienteResponse>>> getAll() {
        log.info("INICIO PETICIÓN: [GET /api/v1/clients] - Solicitud de listado de todos los clientes.");
        List<ClienteResponse> response = clientService.getAll();
        
        log.info("FIN PETICIÓN: [GET /api/v1/clients] - Listado de {} clientes devuelto. Status: 200 OK.", response.size());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponse>> getById(@PathVariable Long id) {
        log.info("INICIO PETICIÓN: [GET /api/v1/clients/{}] - Búsqueda de cliente por ID.", id);
        ClienteResponse response = clientService.getById(id);
        
        log.info("FIN PETICIÓN: [GET /api/v1/clients/{}] - Cliente ID {} encontrado. Status: 200 OK.", id, id);
        return ResponseEntity.ok(ApiResponse.success(clientService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponse>> updateClient(
            @PathVariable Long id,
            @RequestBody @Valid ClienteRequest request) {
        
        log.info("INICIO PETICIÓN: [PUT /api/v1/clients/{}] - Solicitud de actualización de Cliente ID: {}. Identificación: {}.", id, id, request.getIdentificacion());
        ClienteResponse updatedClient = clientService.update(id, request);
        
        log.info("FIN PETICIÓN: [PUT /api/v1/clients/{}] - Cliente ID {} actualizado. Inicia proceso de replicación REST. Status: 200 OK.", id, id);
        return ResponseEntity.ok(ApiResponse.success(updatedClient));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteClient(@PathVariable Long id) {
        log.warn("INICIO PETICIÓN: [DELETE /api/v1/clients/{}] - Solicitud de ELIMINACIÓN LÓGICA de Cliente ID {}.", id, id);
        clientService.delete(id);
        
        log.warn("FIN PETICIÓN: [DELETE /api/v1/clients/{}] - Cliente ID {} marcado como inactivo. Inicia proceso de replicación REST. Status: 200 OK.", id, id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}