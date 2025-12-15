package com.demo.trcuentas.controllers;

import com.demo.trcuentas.domain.dtos.cuenta.CuentaRepositoryPort;
import com.demo.trcuentas.domain.dtos.movimiento.requests.MovimientoRequest;
import com.demo.trcuentas.domain.models.ClienteCuenta;
import com.demo.trcuentas.domain.models.Cuenta;
import com.demo.trcuentas.infrastructure.repositories.cliente.ClienteCuentaJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
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
class IntegrationTestMovimiento {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CuentaRepositoryPort cuentaRepository;

    @Autowired
    private ClienteCuentaJpaRepository clienteRepository;

    private final String CUENTA_NUMERO = "445566";

    @BeforeEach
    void setup() {
        ClienteCuenta cliente = new ClienteCuenta();
        cliente.setId(1L);
        cliente.setNombre("Cliente Test Movimientos");
        cliente.setClienteId("cliente_mov_test");
        cliente.setEstado(true);
        cliente = clienteRepository.save(cliente);

        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(CUENTA_NUMERO);
        cuenta.setTipoCuenta("Ahorros");
        cuenta.setSaldoInicial(new BigDecimal("100.00"));
        cuenta.setEstado(true);

        cuenta.setCliente(cliente);

        cuentaRepository.save(cuenta);
    }

    @Test
    @DisplayName("POST /movimientos - Debería crear un depósito y aumentar saldo (201)")
    void whenCreateCredito_ShouldReturnCreatedAndUpdatedBalance() throws Exception {
        // ARRANGE
        MovimientoRequest request = new MovimientoRequest();
        request.setNumeroCuenta(CUENTA_NUMERO);
        request.setTipoMovimiento("Credito");
        request.setValor(new BigDecimal("50.00"));

        // ACT & ASSERT
        mockMvc.perform(post("/api/v1/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.saldo").value(150.00))
                .andExpect(jsonPath("$.valor").value(50.00))
                .andExpect(jsonPath("$.tipoMovimiento").value("Credito"));

        Cuenta cuentaActualizada = cuentaRepository.findActiveCuentasByNumeroId(CUENTA_NUMERO);

        Assertions.assertNotNull(cuentaActualizada, "La cuenta debería existir");

        Assertions.assertEquals(0, new BigDecimal("150.00").compareTo(cuentaActualizada.getSaldoInicial()),
                "El saldo en base de datos debe ser 150.00");
    }
}