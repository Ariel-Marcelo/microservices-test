package com.demo.trclientes.infrastructure.adapters.out.cliente.external;

import com.demo.trclientes.domain.cliente.ports.out.ClienteExternalServicePort;
import com.demo.trclientes.domain.cliente.models.Client;
import com.demo.trclientes.infrastructure.shared.dtos.ClienteReplica;
import com.demo.trclientes.infrastructure.shared.mappers.RestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class CuentaRestAdapter implements ClienteExternalServicePort {

    private final RestTemplate restTemplate;
    private final RestMapper mapper;

    @Value("${tr-cuentas.url}")
    private String urlAccountService;

    @Override
    public void notifyCreate(Client client) {
        try {
            ClienteReplica dto = mapper.toReplica(client);
            restTemplate.postForEntity(urlAccountService, dto, Void.class);
            log.info("Sincronización exitosa con Cuentas para ID: {}", dto.getClienteId());
        } catch (Exception e) {
            log.error("Error al comunicarse con ms-cuentas: {}", e.getMessage());
            throw new RuntimeException("Error de comunicación: No se pudo replicar el cliente. Transacción cancelada.");
        }
    }

    @Override
    public void notifyUpdate(Long id, Client client) {
        try {
            ClienteReplica dto = mapper.toReplica(client);
            String url = urlAccountService + "/" + id;
            restTemplate.put(url, dto);
            log.info("Actualización REST exitosa para ID: {}", id);
        } catch (Exception e) {
            log.error("Error al actualizar en ms-cuentas: {}", e.getMessage());
            throw new RuntimeException("Error de comunicación al actualizar cliente.");
        }
    }

    @Override
    public void notifyDelete(Long id) {
        try {
            String url = urlAccountService + "/" + id;
            restTemplate.delete(url);
            log.info("Eliminación REST exitosa para ID: {}", id);
        } catch (Exception e) {
            log.error("Error al eliminar en ms-cuentas: {}", e.getMessage());
            throw new RuntimeException("Error de comunicación al eliminar cliente.");
        }
    }
}
