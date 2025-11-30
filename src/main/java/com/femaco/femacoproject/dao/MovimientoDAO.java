package com.femaco.femacoproject.dao;

import com.femaco.femacoproject.model.MovimientoInventario;
import com.femaco.femacoproject.model.enums.TipoMovimiento;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MovimientoDAO {
    boolean registrar(MovimientoInventario movimiento);
    Optional<MovimientoInventario> obtenerPorId(String id);
    List<MovimientoInventario> obtenerPorProducto(String productoId);
    List<MovimientoInventario> obtenerPorFecha(Date fechaInicio, Date fechaFin);
    List<MovimientoInventario> obtenerPorTipo(TipoMovimiento tipo);
    List<MovimientoInventario> obtenerTodos();
    List<MovimientoInventario> obtenerPorUsuario(String usuarioId);
    int obtenerTotalMovimientosPorProducto(String productoId, TipoMovimiento tipo);
    List<MovimientoInventario> obtenerUltimosMovimientos(int limite);
}
