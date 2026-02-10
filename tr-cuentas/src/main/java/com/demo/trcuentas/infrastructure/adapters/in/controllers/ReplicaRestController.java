package com.demo.trcuentas.infrastructure.adapters.in.controllers;

import com.demo.trcuentas.domain.clienteCuenta.ports.in.ClienteReplicaService;
import com.demo.trcuentas.infrastructure.adapters.in.rest.api.ReplicaApi;
import com.demo.trcuentas.infrastructure.adapters.in.rest.dtos.ApiResponseVoid;
import com.demo.trcuentas.infrastructure.adapters.in.rest.dtos.ClienteRequest;
import com.demo.trcuentas.infrastructure.adapters.in.mappers.RestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReplicaRestController implements ReplicaApi {

    private final ClienteReplicaService service;
    private final RestMapper restMapper;

    @Override
    public ResponseEntity<ApiResponseVoid> createOrUpdateReplica(ClienteRequest clienteRequest) {
        log.info("INICIO REPLICA (OpenAPI): [POST /api/v1/rclients]");
        service.saveReplica(restMapper.toDomain(clienteRequest));
        
        ApiResponseVoid response = new ApiResponseVoid();
        response.setStatus(true);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponseVoid> deleteReplica(Long id) {
        log.warn("INICIO REPLICA (OpenAPI): [DELETE /api/v1/rclients/{}]", id);
        service.deleteReplica(id);

        ApiResponseVoid response = new ApiResponseVoid();
        response.setStatus(true);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponseVoid> updateReplica(Long id, ClienteRequest clienteRequest) {
        log.info("INICIO REPLICA (OpenAPI): [PUT /api/v1/rclients/{}]", id);
        service.updateReplica(id, restMapper.toDomain(clienteRequest));

        ApiResponseVoid response = new ApiResponseVoid();
        response.setStatus(true);
        return ResponseEntity.ok(response);
    }
}
