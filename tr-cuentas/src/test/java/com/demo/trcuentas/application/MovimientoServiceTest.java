package com.demo.trcuentas.application.movimiento;

import com.demo.trcuentas.domain.cuenta.CuentaDomain;
import com.demo.trcuentas.domain.cuenta.ports.out.CuentaRepositoryPort;
import com.demo.trcuentas.domain.movimiento.MovimientoDomain;
import com.demo.trcuentas.domain.movimiento.ports.out.MovimientoRepositoryPort;
import com.demo.trcuentas.domain.movimiento.strategies.MovimientoStrategy;
import com.demo.trcuentas.domain.movimiento.strategies.MovimientoStrategyFactory;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceTest {

    @Mock
    private CuentaRepositoryPort cuentaRepository;

    @Mock
    private MovimientoRepositoryPort movimientoRepository;

    @Mock
    private MovimientoStrategyFactory strategyFactory;

    @InjectMocks
    private MovimientoService movimientoService;

    @Test
    @DisplayName("Create: Debería registrar un Depósito y aumentar el saldo")
    void create_ShouldIncreaseBalance_WhenCredito() {
        // ARRANGE
        Long cuentaId = 1L;
        BigDecimal saldoInicial = new BigDecimal("100.00");
        BigDecimal valorCredito = new BigDecimal("50.00");
        BigDecimal saldoFinal = new BigDecimal("150.00");

        CuentaDomain cuentaMock = CuentaDomain.builder()
                .id(cuentaId)
                .saldoInicial(saldoInicial)
                .build();

        MovimientoDomain domainRequest = MovimientoDomain.builder()
                .cuentaId(cuentaId)
                .tipoMovimiento("Credito")
                .valor(valorCredito)
                .build();

        MovimientoStrategy strategyMock = mock(MovimientoStrategy.class);

        when(cuentaRepository.getActiveCuentasById(cuentaId)).thenReturn(cuentaMock);
        when(strategyFactory.getStrategy("Credito")).thenReturn(strategyMock);
        when(strategyMock.calcularNuevoSaldo(saldoInicial, valorCredito)).thenReturn(saldoFinal);
        when(movimientoRepository.save(any(MovimientoDomain.class))).thenAnswer(i -> i.getArgument(0));

        // ACT
        MovimientoDomain response = movimientoService.create(domainRequest);

        // ASSERT
        assertNotNull(response);
        assertEquals(saldoFinal, response.getSaldo());
        assertEquals(valorCredito, response.getValor());
        verify(cuentaRepository).save(cuentaMock);
    }

    @Test
    @DisplayName("Update: Debería actualizar saldo si es el último movimiento")
    void update_ShouldUpdateBalance_WhenIsLastMovement() {
        // ARRANGE
        Long movimientoId = 1L;
        Long cuentaId = 10L;

        CuentaDomain cuentaMock = CuentaDomain.builder()
                .id(cuentaId)
                .saldoInicial(new BigDecimal("150.00"))
                .build();

        MovimientoDomain original = MovimientoDomain.builder()
                .id(movimientoId)
                .cuentaId(cuentaId)
                .tipoMovimiento("Debito")
                .valor(new BigDecimal("-50.00"))
                .build();

        MovimientoDomain updateRequest = MovimientoDomain.builder()
                .tipoMovimiento("Debito")
                .valor(new BigDecimal("20.00"))
                .build();

        MovimientoStrategy strategyMock = mock(MovimientoStrategy.class);

        when(movimientoRepository.getMovimientosById(movimientoId)).thenReturn(original);
        when(movimientoRepository.findLastByCuentaId(cuentaId)).thenReturn(Optional.of(original));
        when(cuentaRepository.getActiveCuentasById(cuentaId)).thenReturn(cuentaMock);
        when(strategyFactory.getStrategy("Debito")).thenReturn(strategyMock);
        
        // saldoBase = 150 - (-50) = 200. Luego 200 - 20 = 180
        when(strategyMock.calcularNuevoSaldo(new BigDecimal("200.00"), new BigDecimal("20.00")))
                .thenReturn(new BigDecimal("180.00"));
        
        when(movimientoRepository.save(any(MovimientoDomain.class))).thenAnswer(i -> i.getArgument(0));

        // ACT
        MovimientoDomain result = movimientoService.update(movimientoId, updateRequest);

        // ASSERT
        assertEquals(new BigDecimal("180.00"), result.getSaldo());
        assertEquals(new BigDecimal("-20.00"), result.getValor());
        verify(cuentaRepository).save(cuentaMock);
    }

    @Test
    @DisplayName("Delete: Debería reversar la transacción")
    void delete_ShouldReverseTransaction() {
        // ARRANGE
        Long movimientoId = 1L;
        Long cuentaId = 10L;
        
        CuentaDomain cuentaMock = CuentaDomain.builder()
                .id(cuentaId)
                .saldoInicial(new BigDecimal("150.00"))
                .build();

        MovimientoDomain original = MovimientoDomain.builder()
                .id(movimientoId)
                .cuentaId(cuentaId)
                .tipoMovimiento("Credito")
                .valor(new BigDecimal("50.00"))
                .build();

        MovimientoStrategy strategyMock = mock(MovimientoStrategy.class);

        when(movimientoRepository.getMovimientosById(movimientoId)).thenReturn(original);
        when(cuentaRepository.getActiveCuentasById(cuentaId)).thenReturn(cuentaMock);
        when(strategyFactory.getStrategy("Debito")).thenReturn(strategyMock);
        when(strategyMock.calcularNuevoSaldo(any(), any())).thenReturn(new BigDecimal("100.00"));

        // ACT
        movimientoService.delete(movimientoId);

        // ASSERT
        assertEquals("Reversado", original.getTipoMovimiento());
        verify(cuentaRepository).save(cuentaMock);
        verify(movimientoRepository, times(2)).save(any(MovimientoDomain.class));
    }
}
