package cl.bch.segurosempresas.mantenedor.service;

import cl.bch.segurosempresas.mantenedor.dto.ResumenSolicitudesRequestDto;
import cl.bch.segurosempresas.mantenedor.dto.ResumenSolicitudesResponseDto;

/**
 * Servicio para consulta de resumen de solicitudes.
 * Alineado con lineamientos BCH: capa de servicio orquesta y normaliza la salida (p.ej. 'codigo').
 */
public interface ResumenSolicitudesService {

    /**
     * Variante principal: recibe DTO de request.
     */
    ResumenSolicitudesResponseDto obtenerResumenSolicitudes(ResumenSolicitudesRequestDto request);

    /**
     * Variante de conveniencia: mismos datos desagregados.
     * Útil para controladores o tests que no quieran construir el DTO.
     */
    ResumenSolicitudesResponseDto obtenerResumen(Integer idUsuario, String fecha, String rol);
}
