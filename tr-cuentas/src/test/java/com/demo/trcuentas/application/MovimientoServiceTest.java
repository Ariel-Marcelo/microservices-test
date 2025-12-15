package com.demo.trcuentas.application;

import com.demo.trcuentas.domain.dtos.cuenta.CuentaRepositoryPort;
import com.demo.trcuentas.domain.dtos.movimiento.MovimientoRepositoryPort;
import com.demo.trcuentas.domain.dtos.movimiento.requests.MovimientoRequest;
import com.demo.trcuentas.domain.dtos.movimiento.responses.MovimientoResponse;
import com.demo.trcuentas.domain.exceptions.LowBalanceException;
import com.demo.trcuentas.domain.models.Cuenta;
import com.demo.trcuentas.domain.models.Movimiento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceTest {

    @Mock
    private CuentaRepositoryPort cuentaRepository;

    @Mock
    private MovimientoRepositoryPort movimientoRepository;

    @InjectMocks
    private MovimientoService movimientoService;

    @Test
    @DisplayName("Create: Debería registrar un Depósito y aumentar el saldo")
    void create_ShouldIncreaseBalance_WhenCredito() {
        // ARRANGE
        String numeroCuenta = "12345";
        BigDecimal saldoInicial = new BigDecimal("100.00");
        BigDecimal valorCredito = new BigDecimal("50.00");

        Cuenta cuentaMock = new Cuenta();
        cuentaMock.setNumeroCuenta(numeroCuenta);
        cuentaMock.setSaldoInicial(saldoInicial);

        MovimientoRequest request = new MovimientoRequest();
        request.setNumeroCuenta(numeroCuenta);
        request.setTipoMovimiento("Credito");
        request.setValor(valorCredito);

        when(cuentaRepository.findActiveCuentasByNumeroId(numeroCuenta)).thenReturn(cuentaMock);
        when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(i -> i.getArgument(0));

        // ACT
        MovimientoResponse response = movimientoService.create(request);

        // ASSERT
        assertNotNull(response);
        assertEquals(new BigDecimal("150.00"), cuentaMock.getSaldoInicial());
        verify(cuentaRepository).save(cuentaMock);
    }

    @Test
    @DisplayName("Create: Debería lanzar LowBalanceException si el Retiro excede el saldo")
    void create_ShouldThrowException_WhenBalanceIsInsufficient() {
        // ARRANGE
        Cuenta cuentaMock = new Cuenta();
        cuentaMock.setSaldoInicial(new BigDecimal("10.00"));

        MovimientoRequest request = new MovimientoRequest();
        request.setNumeroCuenta("12345");
        request.setTipoMovimiento("Debito");
        request.setValor(new BigDecimal("50.00"));

        when(cuentaRepository.findActiveCuentasByNumeroId("12345")).thenReturn(cuentaMock);

        assertThrows(LowBalanceException.class, () -> movimientoService.create(request));

        verify(cuentaRepository, never()).save(any());
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update: Debería actualizar saldo si es el último movimiento")
    void update_ShouldUpdateBalance_WhenIsLastMovement() {
        // ARRANGE
        Long movimientoId = 1L;
        Long cuentaId = 100L;

        Cuenta cuentaMock = new Cuenta();
        cuentaMock.setId(cuentaId);
        cuentaMock.setSaldoInicial(new BigDecimal("150.00"));

        Movimiento movimientoOriginal = new Movimiento();
        movimientoOriginal.setId(movimientoId);
        movimientoOriginal.setTipoMovimiento("Debito");
        movimientoOriginal.setValor(new BigDecimal("-50.00"));
        movimientoOriginal.setCuenta(cuentaMock);

        MovimientoRequest request = new MovimientoRequest();
        request.setTipoMovimiento("Debito");
        request.setValor(new BigDecimal("20.00"));

        when(movimientoRepository.getMovimientosById(movimientoId)).thenReturn(movimientoOriginal);
        when(movimientoRepository.findLastByCuentaId(cuentaId)).thenReturn(Optional.of(movimientoOriginal));
        when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(i -> i.getArgument(0));

        // ACT
        movimientoService.update(movimientoId, request);

        // ASSERT
        assertEquals(new BigDecimal("180.00"), cuentaMock.getSaldoInicial());
        verify(cuentaRepository).save(cuentaMock);
    }

    @Test
    @DisplayName("Update: Debería fallar si NO es el último movimiento")
    void update_ShouldThrowError_WhenNotLastMovement() {
        // ARRANGE
        Long movimientoId = 1L;
        Long ultimoMovimientoId = 2L;
        Long cuentaId = 100L;

        Cuenta cuentaMock = new Cuenta();
        cuentaMock.setId(cuentaId);

        Movimiento movimientoOriginal = new Movimiento();
        movimientoOriginal.setId(movimientoId);
        movimientoOriginal.setCuenta(cuentaMock);

        Movimiento ultimoMovimientoReal = new Movimiento();
        ultimoMovimientoReal.setId(ultimoMovimientoId);

        when(movimientoRepository.getMovimientosById(movimientoId)).thenReturn(movimientoOriginal);
        when(movimientoRepository.findLastByCuentaId(cuentaId)).thenReturn(Optional.of(ultimoMovimientoReal));

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> movimientoService.update(movimientoId, new MovimientoRequest()));

        assertTrue(exception.getMessage().contains("Solo se permite editar el último movimiento"));
    }


    @Test
    @DisplayName("Delete: Debería reversar la transacción creando una contrapartida")
    void delete_ShouldReverseTransaction() {
        // ARRANGE
        Long movimientoId = 1L;
        Cuenta cuentaMock = new Cuenta();
        cuentaMock.setSaldoInicial(new BigDecimal("150.00"));

        Movimiento movimientoOriginal = new Movimiento();
        movimientoOriginal.setId(movimientoId);
        movimientoOriginal.setTipoMovimiento("Credito");
        movimientoOriginal.setValor(new BigDecimal("50.00"));
        movimientoOriginal.setCuenta(cuentaMock);

        when(movimientoRepository.getMovimientosById(movimientoId)).thenReturn(movimientoOriginal);

        // ACT
        movimientoService.delete(movimientoId);

        // ASSERT
        assertEquals(new BigDecimal("100.00"), cuentaMock.getSaldoInicial());

        assertEquals("Reversado", movimientoOriginal.getTipoMovimiento());

        verify(cuentaRepository).save(cuentaMock);
        verify(movimientoRepository, times(2)).save(any(Movimiento.class));
    }
}
