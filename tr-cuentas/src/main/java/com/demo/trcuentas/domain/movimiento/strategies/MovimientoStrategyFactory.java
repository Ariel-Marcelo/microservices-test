package com.demo.trcuentas.domain.movimiento.strategies;

import com.demo.trcuentas.infrastructure.adapters.in.rest.dtos.TipoMovimiento;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MovimientoStrategyFactory {

    private final Map<TipoMovimiento, MovimientoStrategy> strategies;

    public MovimientoStrategyFactory(List<MovimientoStrategy> strategyList) {
        strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        MovimientoStrategy::getTipoMovimiento,
                        Function.identity()
                ));
    }

    public MovimientoStrategy getStrategy(TipoMovimiento tipo) {
        MovimientoStrategy strategy = strategies.get(tipo);
        if (strategy == null) {
            throw new IllegalArgumentException("Tipo de movimiento no soportado: " + tipo);
        }
        return strategy;
    }
}
