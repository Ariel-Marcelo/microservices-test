package com.demo.trcuentas.controllers;

import com.demo.trcuentas.domain.dtos.clienteCuenta.ClienteReplica;
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
    public ResponseEntity<Void> createOrUpdateReplica(@RequestBody ClienteReplica request) {
        service.saveReplica(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateReplica(@PathVariable Long id, @RequestBody ClienteReplica request) {
        service.updateReplica(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReplica(@PathVariable Long id) {
        service.deleteReplica(id);
        return ResponseEntity.noContent().build();
    }
}