package com.demo.trcuentas.infrastructure.adapters.in.cuenta;

import com.demo.trcuentas.application.cuenta.CuentaService;
import com.demo.trcuentas.infrastructure.adapters.in.rest.api.AccountsApi;
import com.demo.trcuentas.infrastructure.adapters.in.rest.dtos.ApiResponseCuentaResponse;
import com.demo.trcuentas.infrastructure.adapters.in.rest.dtos.ApiResponseListCuentaResponse;
import com.demo.trcuentas.infrastructure.adapters.in.rest.dtos.ApiResponseVoid;
import com.demo.trcuentas.infrastructure.adapters.in.rest.dtos.CuentaRequest;
import com.demo.trcuentas.infrastructure.adapters.in.rest.dtos.CuentaResponse;
import com.demo.trcuentas.infrastructure.shared.mappers.RestMapper;

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
public class CuentaRestController implements AccountsApi {

    private final CuentaService cuentaService;
    private final RestMapper restMapper;

    @Override
    public ResponseEntity<ApiResponseCuentaResponse> createAccount(CuentaRequest cuentaRequest) {
        log.info("INICIO PETICIÓN (OpenAPI): [POST /api/v1/accounts] - Solicitud de creación de cuenta.");
        var domainRequest = restMapper.toDomain(cuentaRequest);
        var domainResponse = cuentaService.create(domainRequest);
        var restResponse = restMapper.toRest(domainResponse);

        ApiResponseCuentaResponse response = new ApiResponseCuentaResponse();
        response.setStatus(true);
        response.setData(restResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<ApiResponseVoid> deleteAccount(Long id) {
        log.warn("INICIO PETICIÓN (OpenAPI): [DELETE /api/v1/accounts/{}] - Solicitud de ELIMINACIÓN.", id);
        cuentaService.delete(id);

        ApiResponseVoid response = new ApiResponseVoid();
        response.setStatus(true);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponseCuentaResponse> getAccountById(Long id) {
        log.info("INICIO PETICIÓN (OpenAPI): [GET /api/v1/accounts/{}] - Búsqueda por ID.", id);
        var domainResponse = cuentaService.getById(id);
        var restResponse = restMapper.toRest(domainResponse);

        ApiResponseCuentaResponse response = new ApiResponseCuentaResponse();
        response.setStatus(true);
        response.setData(restResponse);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponseListCuentaResponse> getAllAccounts() {
        log.info("INICIO PETICIÓN (OpenAPI): [GET /api/v1/accounts] - Listado completo.");
        List<CuentaResponse> restList = cuentaService.getAll().stream()
                .map(restMapper::toRest)
                .collect(Collectors.toList());

        ApiResponseListCuentaResponse response = new ApiResponseListCuentaResponse();
        response.setStatus(true);
        response.setData(restList);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponseCuentaResponse> updateAccount(Long id, CuentaRequest cuentaRequest) {
        log.info("INICIO PETICIÓN (OpenAPI): [PUT /api/v1/accounts/{}] - Actualización.", id);
        var domainRequest = restMapper.toDomain(cuentaRequest);
        var domainResponse = cuentaService.update(id, domainRequest);
        var restResponse = restMapper.toRest(domainResponse);

        ApiResponseCuentaResponse response = new ApiResponseCuentaResponse();
        response.setStatus(true);
        response.setData(restResponse);

        return ResponseEntity.ok(response);
    }
}
