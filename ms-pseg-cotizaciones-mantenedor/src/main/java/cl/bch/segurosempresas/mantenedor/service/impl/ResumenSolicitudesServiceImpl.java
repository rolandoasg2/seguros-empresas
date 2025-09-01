package cl.bch.segurosempresas.mantenedor.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cl.bch.segurosempresas.mantenedor.dto.ResumenSolicitudesRequestDto;
import cl.bch.segurosempresas.mantenedor.dto.ResumenSolicitudesResponseDto;
import cl.bch.segurosempresas.mantenedor.repository.ResumenSolicitudesRepository;
import cl.bch.segurosempresas.mantenedor.service.ResumenSolicitudesService;

/**
 * Servicio de orquestación (capa de negocio):
 * - Delegar a repositorio.
 * - Normalizar 'codigo' y 'mensaje' de la API según lineamientos.
 */
@Service
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ResumenSolicitudesServiceImpl implements ResumenSolicitudesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResumenSolicitudesServiceImpl.class);

    private final ResumenSolicitudesRepository repository;

    public ResumenSolicitudesServiceImpl(final ResumenSolicitudesRepository repository) {
        this.repository = repository;
    }

    @Override
    public ResumenSolicitudesResponseDto obtenerResumenSolicitudes(final ResumenSolicitudesRequestDto request) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Service.obtenerResumenSolicitudes({}, {}, {})",
                    request != null ? request.getIdUsuario() : null,
                    request != null ? request.getFecha() : null,
                    request != null ? request.getRol() : null);
        }

        final ResumenSolicitudesResponseDto resp =
                repository.findSolicitudesByUsuarioAndFechaAndRol(
                        request != null ? request.getIdUsuario() : null,
                        request != null ? request.getFecha() : null,
                        request != null ? request.getRol() : null);

        // === Normalización de 'codigo' (0=OK cuando SP devuelve OK/OK; si no 1) ===
        final boolean ok = "OK".equalsIgnoreCase(resp.getEstado())
                && "OK".equalsIgnoreCase(resp.getEstadoCreacion());
        resp.setCodigo(ok ? 0 : 1);

        // Mensaje por defecto si viene vacío
        if (resp.getMensaje() == null || resp.getMensaje().isBlank()) {
            resp.setMensaje(ok ? "Resumen recuperado correctamente"
                               : "Se produjo un error al recuperar el resumen");
        }
        return resp;
    }

    @Override
    public ResumenSolicitudesResponseDto obtenerResumen(final Integer idUsuario,
                                                        final String fecha,
                                                        final String rol) {
        final ResumenSolicitudesRequestDto req = new ResumenSolicitudesRequestDto();
        req.setIdUsuario(idUsuario);
        req.setFecha(fecha);
        req.setRol(rol);
        return obtenerResumenSolicitudes(req);
    }
}
