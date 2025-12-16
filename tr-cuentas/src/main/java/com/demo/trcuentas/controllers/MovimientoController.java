package com.demo.trcuentas.controllers;

import com.demo.trcuentas.application.MovimientoService;
import com.demo.trcuentas.domain.dtos.ApiResponse; // <--- Importar
import com.demo.trcuentas.domain.dtos.movimiento.requests.MovimientoRequest;
import com.demo.trcuentas.domain.dtos.movimiento.responses.MovimientoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/v1/movements") // O "movimientos" según tu preferencia
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService movimientoService;

    @PostMapping
    public ResponseEntity<ApiResponse<MovimientoResponse>> create(@RequestBody @Valid MovimientoRequest request) {
        MovimientoResponse response = movimientoService.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MovimientoResponse>>> getAll() {
        List<MovimientoResponse> response = movimientoService.getAll();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovimientoResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(movimientoService.getById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        movimientoService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MovimientoResponse>> update(
            @PathVariable Long id,
            @RequestBody @Valid MovimientoRequest request) {
        MovimientoResponse updatedMovimiento = movimientoService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(updatedMovimiento));
    }
}