package com.demo.trcuentas.controllers;

import com.demo.trcuentas.application.CuentaService;
import com.demo.trcuentas.domain.dtos.ApiResponse;
import com.demo.trcuentas.domain.dtos.cuenta.requests.CuentaRequest;
import com.demo.trcuentas.domain.dtos.cuenta.responses.CuentaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/v1/accounts")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaService cuentaService;

    @PostMapping
    public ResponseEntity<ApiResponse<CuentaResponse>> create(@RequestBody @Valid CuentaRequest request) {

        CuentaResponse createdCuenta = cuentaService.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdCuenta.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(ApiResponse.success(createdCuenta));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CuentaResponse>>> getAll() {
        List<CuentaResponse> response = cuentaService.getAll();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CuentaResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(cuentaService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CuentaResponse>> update(
            @PathVariable Long id,
            @RequestBody @Valid CuentaRequest request) {

        CuentaResponse updatedCuenta = cuentaService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(updatedCuenta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        cuentaService.delete(id);

        return ResponseEntity.ok(ApiResponse.success(null));
    }
}