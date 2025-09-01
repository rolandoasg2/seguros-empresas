package cl.bch.segurosempresas.mantenedor.service;

import cl.bch.segurosempresas.mantenedor.dto.IngresoSolicitudRequestDto;
import cl.bch.segurosempresas.mantenedor.dto.IngresoSolicitudResponseDto;

public interface IngresoSolicitudService {
    IngresoSolicitudResponseDto ingresarSolicitud(IngresoSolicitudRequestDto request);
}
