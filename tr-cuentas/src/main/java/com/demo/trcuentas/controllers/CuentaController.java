package com.demo.trcuentas.controllers;

import com.demo.trcuentas.application.CuentaService;
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
    public ResponseEntity<CuentaResponse> create(@RequestBody @Valid CuentaRequest request) {

        CuentaResponse createdCuenta = cuentaService.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdCuenta.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdCuenta);
    }

    @GetMapping
    public ResponseEntity<List<CuentaResponse>> getAll() {
        List<CuentaResponse> response = cuentaService.getAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(cuentaService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuentaResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid CuentaRequest request) {

        CuentaResponse updatedCuenta = cuentaService.update(id, request);
        return ResponseEntity.ok(updatedCuenta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cuentaService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
