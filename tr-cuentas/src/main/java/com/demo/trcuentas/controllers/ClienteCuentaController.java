package com.demo.trcuentas.controllers;

import com.demo.trcuentas.domain.dtos.ApiResponse;
import com.demo.trcuentas.domain.dtos.clienteCuenta.ClienteRequest;
import com.demo.trcuentas.domain.dtos.clienteCuenta.ClienteReplicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/rclients")
@RequiredArgsConstructor
public class ClienteCuentaController {

    private final ClienteReplicaService service;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createOrUpdateReplica(@RequestBody ClienteRequest request) {
        log.info("INICIO REPLICA: [POST /api/v1/rclients] - Recibiendo datos de creación/actualización para Cliente ID: {}", request.getId());
        service.saveReplica(request);
        log.info("FIN REPLICA: [POST /api/v1/rclients] - Réplica de Cliente ID {} procesada con éxito. Status: 200 OK.", request.getId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateReplica(@PathVariable Long id, @RequestBody ClienteRequest request) {
        log.info("INICIO REPLICA: [PUT /api/v1/rclients] - Recibiendo datos de creación/actualización para Cliente ID: {}", request.getId());
        service.updateReplica(id, request);
        log.info("FIN REPLICA: [PUT /api/v1/rclients] - Réplica de Cliente ID {} procesada con éxito. Status: 200 OK.", request.getId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReplica(@PathVariable Long id) {
        log.warn("INICIO REPLICA: [DELETE /api/v1/rclients/{}] - Solicitud de ELIMINACIÓN LÓGICA para Cliente ID: {}", id, id);
        service.deleteReplica(id);
        log.warn("FIN REPLICA: [DELETE /api/v1/rclients/{}] - Cliente ID {} inhabilitado con éxito. Status: 200 OK.", id, id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}