package cl.bch.segurosempresas.mantenedor.repository;

import cl.bch.segurosempresas.mantenedor.dto.IngresoSolicitudResponseDto;

public interface IngresoSolicitudRepository {
    IngresoSolicitudResponseDto ejecutarSpIngresoSolicitud(String payloadJson);
}
