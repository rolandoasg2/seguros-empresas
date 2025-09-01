package cl.bch.segurosempresas.mantenedor.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Ping de aplicación (NO reemplaza a Actuator).
 * Según el arquetipo, la salud real se expone vía /actuator/health.
 * Este endpoint es solo para verificación rápida de disponibilidad de la app.
 */
@RestController
@RequestMapping(path = "/api/ms/v1", produces = MediaType.TEXT_PLAIN_VALUE) // ← base unificada: /api/ms/v1
public class PingController {

    /** Logger con pattern compatible con Dynatrace (ver logback del arquetipo). */
    private static final Logger LOGGER = LoggerFactory.getLogger(PingController.class);

    /**
     * GET /api/ms/v1/ping
     * @return "OK" si la aplicación está respondiendo.
     */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("GET /api/ms/v1/ping");
        }
        return ResponseEntity.ok("OK");
    }

    // ⚠️ SOLO PARA PRUEBAS LOCALES. COMENTAR/ELIMINAR ANTES DE ENTREGAR.
    @GetMapping("/props")
    public String props(@Value("${app.url_api1:undefined}") String url) {
        return "url_api1=" + url;
    }
}
