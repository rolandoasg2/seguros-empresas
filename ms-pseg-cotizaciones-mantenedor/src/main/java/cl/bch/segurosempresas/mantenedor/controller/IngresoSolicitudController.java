package cl.bch.segurosempresas.mantenedor.controller;

import cl.bch.segurosempresas.mantenedor.dto.IngresoSolicitudRequestDto;
import cl.bch.segurosempresas.mantenedor.dto.IngresoSolicitudResponseDto;
import cl.bch.segurosempresas.mantenedor.service.IngresoSolicitudService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/ms/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class IngresoSolicitudController {

    private static final Logger LOGGER = LoggerFactory.getLogger(IngresoSolicitudController.class);

    private final IngresoSolicitudService ingresoSolicitudService;

    public IngresoSolicitudController(final IngresoSolicitudService ingresoSolicitudService) {
        this.ingresoSolicitudService = ingresoSolicitudService;
    }

    @PostMapping(path = "/ingresoSolicitud", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IngresoSolicitudResponseDto> ingresoSolicitud(
            @Valid @RequestBody final IngresoSolicitudRequestDto request) {

        try {
            LOGGER.info("POST /api/ms/v1/ingresoSolicitud - inicio");
            final IngresoSolicitudResponseDto respuesta = ingresoSolicitudService.ingresarSolicitud(request);
            return ResponseEntity.ok(respuesta);

        } catch (IllegalArgumentException e) {
            LOGGER.warn("Validación de negocio fallida: {}", e.getMessage());
            final IngresoSolicitudResponseDto error = new IngresoSolicitudResponseDto();
            error.setCodigo(HttpStatus.BAD_REQUEST.value());
            error.setEstado("ERROR_VALIDACION");   // ← convención banco para 400
            error.setEstadoCreacion("ERROR");
            error.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

        } catch (Exception ex) {
            LOGGER.error("Error inesperado al procesar ingreso de solicitud", ex);
            final IngresoSolicitudResponseDto error = new IngresoSolicitudResponseDto();
            error.setCodigo(HttpStatus.INTERNAL_SERVER_ERROR.value());
            error.setEstado("ERROR");              // ← convención banco para 500
            error.setEstadoCreacion("ERROR");
            error.setMensaje("Error al procesar la solicitud");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
