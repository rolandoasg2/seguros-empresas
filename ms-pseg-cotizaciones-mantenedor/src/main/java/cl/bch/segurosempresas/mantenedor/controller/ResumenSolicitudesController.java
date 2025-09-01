package cl.bch.segurosempresas.mantenedor.controller;

import cl.bch.segurosempresas.mantenedor.dto.ResumenSolicitudesRequestDto;
import cl.bch.segurosempresas.mantenedor.dto.ResumenSolicitudesResponseDto;
import cl.bch.segurosempresas.mantenedor.service.ResumenSolicitudesService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/ms/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class ResumenSolicitudesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResumenSolicitudesController.class);

    private final ResumenSolicitudesService service;

    public ResumenSolicitudesController(final ResumenSolicitudesService service) {
        this.service = service;
    }

    @PostMapping(path = "/resumenSolicitudes", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResumenSolicitudesResponseDto> obtenerResumen(
            @Valid @RequestBody final ResumenSolicitudesRequestDto request) {

        try {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("POST /api/ms/v1/resumenSolicitudes idUsuario={}, fecha='{}', rol={}",
                        request != null ? request.getIdUsuario() : null,
                        request != null ? request.getFecha() : null,
                        request != null ? request.getRol() : null);
            }

            final ResumenSolicitudesResponseDto resp = service.obtenerResumenSolicitudes(request);
            return ResponseEntity.ok(resp);

        } catch (IllegalArgumentException e) {
            LOGGER.warn("Validación de entrada fallida: {}", e.getMessage());
            final ResumenSolicitudesResponseDto error = new ResumenSolicitudesResponseDto();
            error.setCodigo(HttpStatus.BAD_REQUEST.value());
            error.setEstado("ERROR_VALIDACION");   // ← convención banco para 400
            error.setEstadoCreacion("ERROR");
            error.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

        } catch (Exception ex) {
            LOGGER.error("Error procesando resumen de solicitudes", ex);
            final ResumenSolicitudesResponseDto error = new ResumenSolicitudesResponseDto();
            error.setCodigo(HttpStatus.INTERNAL_SERVER_ERROR.value());
            error.setEstado("ERROR");              // ← convención banco para 500
            error.setEstadoCreacion("ERROR");
            error.setMensaje("Error al procesar la solicitud");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    
    /**
     * GET /api/ms/v1/test
     * @return "OK" si la aplicación está respondiendo.
     */
    @GetMapping("/test")
    public ResponseEntity<String> ping() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("GET /api/ms/v1/test");
        }
        return ResponseEntity.ok("OK");
    }
    
}
