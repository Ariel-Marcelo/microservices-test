package com.demo.trclientes.controllers;

import com.demo.trclientes.domain.dtos.ApiResponse; // Importar
import com.demo.trclientes.domain.dtos.cliente.ClienteServicePort;
import com.demo.trclientes.domain.dtos.cliente.requests.ClienteRequest;
import com.demo.trclientes.domain.dtos.cliente.responses.ClienteResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteServicePort clientService;

    @PostMapping
    public ResponseEntity<ApiResponse<ClienteResponse>> createClient(@Valid @RequestBody ClienteRequest clienteRequest) {
        ClienteResponse createdClient = clientService.create(clienteRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdClient));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClienteResponse>>> getAll() {
        List<ClienteResponse> response = clientService.getAll();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(clientService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponse>> updateClient(
            @PathVariable Long id,
            @RequestBody @Valid ClienteRequest request) {

        ClienteResponse updatedClient = clientService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(updatedClient));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteClient(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}