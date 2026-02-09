package com.demo.trcuentas.domain.movimiento;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MovimientoStrategyFactory {

    private final Map<String, MovimientoStrategy> strategies;

    public MovimientoStrategyFactory(List<MovimientoStrategy> strategyList) {
        strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        s -> s.getTipoMovimiento().toLowerCase(),
                        Function.identity()
                ));
    }

    public MovimientoStrategy getStrategy(String tipo) {
        MovimientoStrategy strategy = strategies.get(tipo.toLowerCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Tipo de movimiento no soportado: " + tipo);
        }
        return strategy;
    }
}
