package com.demo.trcuentas.controllers;

import com.demo.trcuentas.domain.dtos.ApiResponse;
import com.demo.trcuentas.domain.dtos.clienteCuenta.ClienteRequest;
import com.demo.trcuentas.domain.dtos.clienteCuenta.ClienteReplicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rclients")
@RequiredArgsConstructor
public class ClienteCuentaController {

    private final ClienteReplicaService service;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createOrUpdateReplica(@RequestBody ClienteRequest request) {
        service.saveReplica(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateReplica(@PathVariable Long id, @RequestBody ClienteRequest request) {
        service.updateReplica(id, request);

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReplica(@PathVariable Long id) {
        service.deleteReplica(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}