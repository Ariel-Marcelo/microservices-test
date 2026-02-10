package com.demo.trcuentas.controllers;

import com.demo.trcuentas.domain.clienteCuenta.ClienteDomain;
import com.demo.trcuentas.domain.clienteCuenta.ports.out.ClienteReplicaRepositoryPort;
import com.demo.trcuentas.domain.cuenta.CuentaDomain;
import com.demo.trcuentas.domain.cuenta.ports.out.CuentaRepositoryPort;
import com.demo.trcuentas.infrastructure.adapters.in.rest.dtos.MovimientoRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MovimientoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CuentaRepositoryPort cuentaRepository;

    @Autowired
    private ClienteReplicaRepositoryPort clienteRepository;

    @BeforeEach
    void setup() {
        ClienteDomain cliente = ClienteDomain.builder()
                .id(1L) // ID MANUAL REQUERIDO
                .nombre("Test")
                .clienteId("cliente_123")
                .estado(true)
                .build();
        
        cliente = clienteRepository.save(cliente);

        cuentaRepository.save(CuentaDomain.builder()
                .numeroCuenta("111")
                .tipoCuenta("Ahorros")
                .saldoInicial(new BigDecimal("100.00"))
                .estado(true)
                .clienteId(cliente.getId())
                .build());
    }

    @Test
    @DisplayName("POST /movements - Smoke Test")
    void smokeTest() throws Exception {
        MovimientoRequest request = new MovimientoRequest();
        request.setNumeroCuenta("111");
        request.setTipoMovimiento("Credito");
        request.setValor(new BigDecimal("50.00"));

        mockMvc.perform(post("/api/v1/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
