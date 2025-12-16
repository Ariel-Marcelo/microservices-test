package com.demo.trcuentas.controllers;

import com.demo.trcuentas.application.CuentaService;
import com.demo.trcuentas.domain.dtos.ApiResponse;
import com.demo.trcuentas.domain.dtos.cuenta.requests.CuentaRequest;
import com.demo.trcuentas.domain.dtos.cuenta.responses.CuentaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/accounts")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaService cuentaService;

    @PostMapping
    public ResponseEntity<ApiResponse<CuentaResponse>> create(@RequestBody @Valid CuentaRequest request) {
        log.info("INICIO PETICIÓN: [POST /api/v1/accounts] - Solicitud de creación de cuenta para Cliente ID: {}", request.getClienteId());
        CuentaResponse createdCuenta = cuentaService.create(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdCuenta.getId())
                .toUri();

        log.info("FIN PETICIÓN: [POST /api/v1/accounts] - Cuenta {} creada exitosamente para Cliente {}. Status: 201 Created.", createdCuenta.getNumeroCuenta(), request.getClienteId());
        return ResponseEntity.created(location)
                .body(ApiResponse.success(createdCuenta));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CuentaResponse>>> getAll() {
        log.info("INICIO PETICIÓN: [GET /api/v1/accounts] - Solicitud de listado de todas las cuentas.");
        List<CuentaResponse> response = cuentaService.getAll();

        log.info("FIN PETICIÓN: [GET /api/v1/accounts] - Listado de {} cuentas devuelto. Status: 200 OK.", response.size());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CuentaResponse>> getById(@PathVariable Long id) {
        log.info("INICIO PETICIÓN: [GET /api/v1/accounts/{}] - Búsqueda de cuenta por ID.", id);

        CuentaResponse response = cuentaService.getById(id);
        
        log.info("FIN PETICIÓN: [GET /api/v1/accounts/{}] - Cuenta {} encontrada y devuelta. Status: 200 OK.", id, response.getNumeroCuenta());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CuentaResponse>> update(
            @PathVariable Long id,
            @RequestBody @Valid CuentaRequest request) {
        
        log.info("INICIO PETICIÓN: [PUT /api/v1/accounts/{}] - Solicitud de actualización de cuenta {}.", id, request.getNumeroCuenta());
        CuentaResponse updatedCuenta = cuentaService.update(id, request);
        
        log.info("FIN PETICIÓN: [PUT /api/v1/accounts/{}] - Cuenta {} actualizada exitosamente. Status: 200 OK.", id, updatedCuenta.getNumeroCuenta());
        return ResponseEntity.ok(ApiResponse.success(updatedCuenta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        log.warn("INICIO PETICIÓN: [DELETE /api/v1/accounts/{}] - Solicitud de ELIMINACIÓN LÓGICA de cuenta.", id);
        cuentaService.delete(id);
        
        log.warn("FIN PETICIÓN: [DELETE /api/v1/accounts/{}] - Cuenta ID {} marcada como inactiva. Status: 200 OK.", id, id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}