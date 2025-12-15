package com.demo.trcuentas.application;

import com.demo.trcuentas.domain.dtos.cuenta.CuentaMapper;
import com.demo.trcuentas.domain.dtos.cuenta.CuentaRepositoryPort;
import com.demo.trcuentas.domain.dtos.cuenta.CuentaServicePort;
import com.demo.trcuentas.domain.dtos.cuenta.requests.CuentaRequest;
import com.demo.trcuentas.domain.dtos.cuenta.responses.CuentaResponse;
import com.demo.trcuentas.domain.dtos.movimiento.MovimientoRepositoryPort;
import com.demo.trcuentas.domain.models.ClienteCuenta;
import com.demo.trcuentas.domain.models.Cuenta;
import com.demo.trcuentas.domain.models.Movimiento;
import com.demo.trcuentas.infrastructure.repositories.cliente.ClienteCuentaJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CuentaService implements CuentaServicePort {

    private final CuentaRepositoryPort cuentaRepository;
    private final MovimientoRepositoryPort movimientoRepository;
    private final ClienteCuentaJpaRepository clienteCuentaJpaRepository;

    @Override
    public CuentaResponse create(CuentaRequest cuentaRequest) {

        ClienteCuenta cliente =  clienteCuentaJpaRepository.findByIdAndEstadoTrue(cuentaRequest.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado o inactivo con ID: " + cuentaRequest.getClienteId()));

        Cuenta cuenta = CuentaMapper.INSTANCE.toEntity(cuentaRequest);
        cuenta.setCliente(cliente);
        Cuenta savedCuenta = cuentaRepository.save(cuenta);

        if (savedCuenta.getSaldoInicial().compareTo(BigDecimal.ZERO) > 0) {
            Movimiento movimientoInicial = new Movimiento();
            movimientoInicial.setFecha(java.time.LocalDateTime.now());
            movimientoInicial.setTipoMovimiento("Credito");
            movimientoInicial.setValor(savedCuenta.getSaldoInicial());
            movimientoInicial.setSaldo(savedCuenta.getSaldoInicial());
            movimientoInicial.setCuenta(savedCuenta);
            movimientoRepository.save(movimientoInicial);
        }

        return CuentaMapper.INSTANCE.toResponse(savedCuenta);
    }

    @Override
    public List<CuentaResponse> getAll() {
        return cuentaRepository.getAllActiveCuentas().stream().map(CuentaMapper.INSTANCE::toResponse).collect(Collectors.toList());
    }

    @Override
    public CuentaResponse getById(Long id) {
        return CuentaMapper.INSTANCE.toResponse(cuentaRepository.getActiveCuentasById(id));
    }

    @Override
    public CuentaResponse update(Long id, CuentaRequest cuentaRequest) {

        ClienteCuenta cliente = clienteCuentaJpaRepository.findByIdAndEstadoTrue(cuentaRequest.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado o inactivo con ID: " + cuentaRequest.getClienteId()));

        Cuenta cuenta = cuentaRepository.getActiveCuentasById(id);
        BigDecimal pastAmount = cuenta.getSaldoInicial();
        CuentaMapper.INSTANCE.updateEntityFromRequest(cuentaRequest, cuenta);
        cuenta.setCliente(cliente);
        Cuenta cuentaUpdated = cuentaRepository.save(cuenta);
        BigDecimal newAmount = cuentaUpdated.getSaldoInicial();

        if (pastAmount.compareTo(newAmount) != 0) {
            String tipoMovement = "Credito";
            if (pastAmount.compareTo(newAmount) > 0) {
                tipoMovement = "Debito";
            }
            BigDecimal diferencia = newAmount.subtract(pastAmount);
            Movimiento ajuste = new Movimiento();
            ajuste.setFecha(java.time.LocalDateTime.now());
            ajuste.setTipoMovimiento(tipoMovement);
            ajuste.setValor(diferencia);
            ajuste.setSaldo(newAmount);
            ajuste.setCuenta(cuentaUpdated);

            movimientoRepository.save(ajuste);
        }

        return CuentaMapper.INSTANCE.toResponse(cuentaUpdated);
    }

    @Override
    public void delete(Long id) {
        Cuenta cuenta = cuentaRepository.getActiveCuentasById(id);
        cuenta.setEstado(false);
        cuentaRepository.save(cuenta);
    }
}