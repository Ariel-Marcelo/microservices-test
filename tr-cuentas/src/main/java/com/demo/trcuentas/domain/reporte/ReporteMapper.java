package com.demo.trcuentas.domain.reporte;

import com.demo.trcuentas.domain.reporte.response.ReporteCuenta;
import com.demo.trcuentas.domain.reporte.response.ReporteMovimiento;
import com.demo.trcuentas.domain.models.Cuenta;
import com.demo.trcuentas.domain.models.Movimiento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ReporteMapper {

    ReporteMapper INSTANCE = Mappers.getMapper(ReporteMapper.class);

    ReporteMovimiento toMovimientoDto(Movimiento movimiento);

    @Mapping(source = "saldoInicial", target = "saldoActual")
    @Mapping(target = "movimientos", ignore = true)
    ReporteCuenta toCuentaDto(Cuenta cuenta);
}
