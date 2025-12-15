package com.demo.trcuentas.infrastructure;

import com.demo.trcuentas.config.RabbitConfig;
import com.demo.trcuentas.domain.events.ClienteDeletedEvent;
import com.demo.trcuentas.domain.events.ClienteUpdatedEvent;
import com.demo.trcuentas.domain.models.ClienteCuenta;
import com.demo.trcuentas.domain.events.ClienteCreatedEvent;
import com.demo.trcuentas.infrastructure.repositories.cliente.ClienteCuentaJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClienteEventListener {

    private final ClienteCuentaJpaRepository clienteCuentaJpaRepository;

    @Transactional
    @RabbitListener(
            queues = RabbitConfig.CREATE_QUEUE,
            concurrency = "5"
    )
    public void processClienteCreatedEvent(ClienteCreatedEvent event) {
        log.info("EVENTO RECIBIDO: Replicando cliente con ID de Negocio: {}", event.getClienteId());
        try {
            ClienteCuenta cliente = new ClienteCuenta();
            cliente.setId(event.getId());
            cliente.setClienteId(event.getClienteId());
            cliente.setNombre(event.getNombre());
            cliente.setEstado(true);
            clienteCuentaJpaRepository.save(cliente);
            log.info("Replicación exitosa para ID {}", event.getId());
        } catch (DataAccessException e) {
            log.error("Error TRANSITORIO (DB) al crear cliente {}. Reintentando...", event.getId(), e.getMessage());
        } catch (Exception e) {
            log.error("Error PERMANENTE al crear cliente {}. Enviando a DLQ.", event.getId(), e);
            throw new AmqpRejectAndDontRequeueException(e);
        }

    }

    @Transactional
    @RabbitListener(
            queues = RabbitConfig.UPDATE_QUEUE,
            concurrency = "5"
    )
    public void processClienteUpdatedEvent(ClienteUpdatedEvent event) {
        log.info("EVENTO RECIBIDO: Actualizando cliente con ID de Negocio: {}", event.getClienteId());
        try {
            ClienteCuenta cliente = clienteCuentaJpaRepository.getById(event.getId());
            cliente.setClienteId(event.getClienteId());
            cliente.setNombre(event.getNombre());

            clienteCuentaJpaRepository.save(cliente);
            log.info("Actualización exitosa para ID {}", event.getId());

        } catch (EntityNotFoundException e) {
            log.warn("Error de Consistencia (Cliente ID {}) no existe. Reintentando...", event.getId());
            throw e;
        } catch (DataAccessException e) {
            log.error("Error TRANSITORIO (DB) al actualizar cliente {}. Reintentando...", event.getId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error PERMANENTE al actualizar cliente {}. Enviando a DLQ.", event.getId(), e);
            throw new AmqpRejectAndDontRequeueException(e);
        }

    }

    @Transactional
    @RabbitListener(
            queues = RabbitConfig.DELETE_QUEUE,
            concurrency = "5"
    )
    public void processClienteDeletedEvent(ClienteDeletedEvent event) {
        log.info("EVENTO RECIBIDO: Inhabilitando cliente con ID: {}", event.getId());
        try {
            ClienteCuenta cliente = clienteCuentaJpaRepository.getById(event.getId());
            cliente.setEstado(false);
            clienteCuentaJpaRepository.save(cliente);
            log.info("Inhabilitación exitosa para ID {}", event.getId());
        } catch (DataAccessException e) {
            log.error("Error TRANSITORIO (DB) al eliminar cliente {}. Reintentando...", event.getId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error PERMANENTE al eliminar cliente {}. Enviando a DLQ.", event.getId(), e);
            throw new AmqpRejectAndDontRequeueException(e);
        }

    }
}
