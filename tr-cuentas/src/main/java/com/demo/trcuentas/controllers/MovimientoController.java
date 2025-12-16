package com.demo.trcuentas.controllers;

import com.demo.trcuentas.application.MovimientoService;
import com.demo.trcuentas.domain.dtos.ApiResponse;
import com.demo.trcuentas.domain.dtos.movimiento.requests.MovimientoRequest;
import com.demo.trcuentas.domain.dtos.movimiento.responses.MovimientoResponse;
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
@RequestMapping("api/v1/movements")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService movimientoService;

    @PostMapping
    public ResponseEntity<ApiResponse<MovimientoResponse>> create(@RequestBody @Valid MovimientoRequest request) {
        log.info("INICIO PETICIÓN: [POST /api/v1/movements] - Solicitud de {} por valor {} en Cuenta: {}", request.getTipoMovimiento(), request.getValor(), request.getNumeroCuenta());
        MovimientoResponse response = movimientoService.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        log.info("FIN PETICIÓN: [POST /api/v1/movements] - Movimiento ID {} creado exitosamente. Saldo final: {}. Status: 201 Created.", response.getId(), response.getSaldo());
        return ResponseEntity.created(location)
                .body(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MovimientoResponse>>> getAll() {
        log.info("INICIO PETICIÓN: [GET /api/v1/movements] - Solicitud de listado de todos los movimientos.");
        List<MovimientoResponse> response = movimientoService.getAll();

        log.info("FIN PETICIÓN: [GET /api/v1/movements] - Listado de {} movimientos devuelto. Status: 200 OK.", response.size());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovimientoResponse>> getById(@PathVariable Long id) {
        log.info("INICIO PETICIÓN: [GET /api/v1/movements/{}] - Búsqueda de movimiento por ID.", id);
        MovimientoResponse response = movimientoService.getById(id);

        log.info("FIN PETICIÓN: [GET /api/v1/movements/{}] - Movimiento ID {} encontrado. Status: 200 OK.", id, id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        log.warn("INICIO PETICIÓN: [DELETE /api/v1/movements/{}] - Solicitud de REVERSIÓN de movimiento ID {}.", id, id);
        movimientoService.delete(id);

        log.warn("FIN PETICIÓN: [DELETE /api/v1/movements/{}] - Movimiento ID {} reversado exitosamente. Status: 200 OK.", id, id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MovimientoResponse>> update(
            @PathVariable Long id,
            @RequestBody @Valid MovimientoRequest request) {
        log.warn("INICIO PETICIÓN: [PUT /api/v1/movements/{}] - Solicitud de ACTUALIZACIÓN de movimiento ID {} a valor {}.", id, id, request.getValor());
        MovimientoResponse updatedMovimiento = movimientoService.update(id, request);

        log.warn("FIN PETICIÓN: [PUT /api/v1/movements/{}] - Movimiento ID {} actualizado. Saldo final: {}. Status: 200 OK.", id, id, updatedMovimiento.getSaldo());
        return ResponseEntity.ok(ApiResponse.success(updatedMovimiento));
    }
}