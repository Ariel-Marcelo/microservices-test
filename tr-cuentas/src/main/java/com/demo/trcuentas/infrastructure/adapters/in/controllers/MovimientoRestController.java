package com.demo.trcuentas.infrastructure.adapters.in.controllers;

import com.demo.trcuentas.application.MovimientoService;
import com.demo.trcuentas.infrastructure.adapters.in.rest.api.MovementsApi;
import com.demo.trcuentas.domain.dtos.ApiResponseListMovimientoResponse;
import com.demo.trcuentas.domain.dtos.ApiResponseMovimientoResponse;
import com.demo.trcuentas.domain.dtos.ApiResponseVoid;
import com.demo.trcuentas.domain.dtos.MovimientoRequest;
import com.demo.trcuentas.domain.dtos.MovimientoResponse;
import com.demo.trcuentas.infrastructure.adapters.in.mappers.RestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MovimientoRestController implements MovementsApi {

    private final MovimientoService movimientoService;
    private final RestMapper restMapper;

    @Override
    public ResponseEntity<ApiResponseMovimientoResponse> createMovement(MovimientoRequest movimientoRequest) {
        log.info("INICIO PETICIÓN (OpenAPI): [POST /api/v1/movements]");
        var domainRequest = restMapper.toDomain(movimientoRequest);
        var domainResponse = movimientoService.create(domainRequest);
        var restResponse = restMapper.toRest(domainResponse);

        ApiResponseMovimientoResponse response = new ApiResponseMovimientoResponse();
        response.setStatus(true);
        response.setData(restResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<ApiResponseVoid> deleteMovement(Long id) {
        log.warn("INICIO PETICIÓN (OpenAPI): [DELETE /api/v1/movements/{}]", id);
        movimientoService.delete(id);

        ApiResponseVoid response = new ApiResponseVoid();
        response.setStatus(true);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponseListMovimientoResponse> getAllMovements() {
        log.info("INICIO PETICIÓN (OpenAPI): [GET /api/v1/movements]");
        List<MovimientoResponse> restList = movimientoService.getAll().stream()
                .map(restMapper::toRest)
                .collect(Collectors.toList());

        ApiResponseListMovimientoResponse response = new ApiResponseListMovimientoResponse();
        response.setStatus(true);
        response.setData(restList);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponseMovimientoResponse> getMovementById(Long id) {
        log.info("INICIO PETICIÓN (OpenAPI): [GET /api/v1/movements/{}]", id);
        var domainResponse = movimientoService.getById(id);
        var restResponse = restMapper.toRest(domainResponse);

        ApiResponseMovimientoResponse response = new ApiResponseMovimientoResponse();
        response.setStatus(true);
        response.setData(restResponse);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponseMovimientoResponse> updateMovement(Long id, MovimientoRequest movimientoRequest) {
        log.info("INICIO PETICIÓN (OpenAPI): [PUT /api/v1/movements/{}]", id);
        var domainRequest = restMapper.toDomain(movimientoRequest);
        var domainResponse = movimientoService.update(id, domainRequest);
        var restResponse = restMapper.toRest(domainResponse);

        ApiResponseMovimientoResponse response = new ApiResponseMovimientoResponse();
        response.setStatus(true);
        response.setData(restResponse);

        return ResponseEntity.ok(response);
    }
}