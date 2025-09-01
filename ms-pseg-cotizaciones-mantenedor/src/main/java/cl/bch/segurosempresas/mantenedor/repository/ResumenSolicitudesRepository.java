package cl.bch.segurosempresas.mantenedor.repository;

import cl.bch.segurosempresas.mantenedor.dto.ResumenSolicitudesResponseDto;

/**
 * Repositorio para consultar el resumen de solicitudes vía Stored Procedures.
 * Alineado con arquetipo BCH: capa Repository separada de Service/Controller.
 */
public interface ResumenSolicitudesRepository {

    /**
     * Ejecuta el SP de resumen según rol.
     * @param idUsuario id del usuario
     * @param fecha     fecha en String (ej: yyyy-MM-dd)
     * @param rol       rol del usuario (EJE, SUP, COOR)
     * @return respuesta de negocio
     */
    ResumenSolicitudesResponseDto findSolicitudesByUsuarioAndFechaAndRol(
            Integer idUsuario, String fecha, String rol);
}
