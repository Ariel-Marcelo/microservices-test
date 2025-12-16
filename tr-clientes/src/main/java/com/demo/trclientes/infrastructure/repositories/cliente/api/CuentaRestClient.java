package com.demo.trclientes.infrastructure.repositories.cliente.api;

import com.demo.trclientes.domain.dtos.cliente.replica.ClienteReplica;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class CuentaRestClient {

    private final RestTemplate restTemplate;

    @Value("${tr-cuentas.url}")
    private String urlCuentas;

    public void notifyCreate(ClienteReplica dto) {
        try {
            restTemplate.postForEntity(urlCuentas, dto, Void.class);
            log.info("Sincronización exitosa con Cuentas para ID: {}", dto.getClienteId());
        } catch (Exception e) {
            log.error("Error al comunicarse con ms-cuentas: {}", e.getMessage());
            throw new RuntimeException("Error de comunicación: No se pudo replicar el cliente. Transacción cancelada.");
        }
    }

    public void notifyUpdate(Long id, ClienteReplica dto) {
        try {
            String url = urlCuentas + "/" + id;
            restTemplate.put(url, dto);
            log.info("Actualización REST exitosa para ID: {}", id);
        } catch (Exception e) {
            log.error("Error al actualizar en ms-cuentas: {}", e.getMessage());
            throw new RuntimeException("Error de comunicación al actualizar cliente.");
        }
    }

    public void notifyDelete(Long id) {
        try {
            String url = urlCuentas + "/" + id;
            restTemplate.delete(url);
            log.info("Eliminación REST exitosa para ID: {}", id);
        } catch (Exception e) {
            log.error("Error al eliminar en ms-cuentas: {}", e.getMessage());
            throw new RuntimeException("Error de comunicación al eliminar cliente.");
        }
    }
}
