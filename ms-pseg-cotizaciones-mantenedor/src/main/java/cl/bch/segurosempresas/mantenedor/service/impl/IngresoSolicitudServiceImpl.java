package cl.bch.segurosempresas.mantenedor.service.impl;

import cl.bch.segurosempresas.mantenedor.dto.IngresoSolicitudRequestDto;
import cl.bch.segurosempresas.mantenedor.dto.IngresoSolicitudResponseDto;
import cl.bch.segurosempresas.mantenedor.repository.IngresoSolicitudRepository;
import cl.bch.segurosempresas.mantenedor.service.IngresoSolicitudService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class IngresoSolicitudServiceImpl implements IngresoSolicitudService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IngresoSolicitudServiceImpl.class);

    private final IngresoSolicitudRepository repository;
    private final ObjectMapper mapper = new ObjectMapper();

    public IngresoSolicitudServiceImpl(final IngresoSolicitudRepository repository) {
        this.repository = repository;
    }

    @Override
    public IngresoSolicitudResponseDto ingresarSolicitud(final IngresoSolicitudRequestDto request) {
        // Validación mínima (400) sin depender de getters del DTO
        validarReqComoJson(request);

        try {
            final String payload = buildSpPayload(request);
            LOGGER.info("Invocando SP spCSEingresoSolicitud_rolo");
            final IngresoSolicitudResponseDto resp = repository.ejecutarSpIngresoSolicitud(payload);

            if (!"OK".equalsIgnoreCase(resp.getEstado())) {
                throw new IllegalStateException("Error interno al registrar la solicitud: " + resp.getMensaje());
            }
            return resp;

        } catch (IllegalArgumentException e) {
            throw e; // 400
        } catch (Exception e) {
            LOGGER.error("Error al invocar el SP de ingreso de solicitud", e);
            throw new IllegalStateException("Error interno al registrar la solicitud", e);
        }
    }

    // =====================================================================
    // Validación sin getters: convertimos a JsonNode y revisamos presencia.
    // =====================================================================
    private void validarReqComoJson(IngresoSolicitudRequestDto request) {
        if (request == null) {
            throw new IllegalArgumentException("Solicitud nula");
        }
        JsonNode root = mapper.valueToTree(request);
        if (root == null || root.isNull()) {
            throw new IllegalArgumentException("Solicitud nula");
        }
        if (root.get("cuestionario") == null || root.get("cuestionario").isNull()) {
            throw new IllegalArgumentException("Cuestionario es obligatorio");
        }
        JsonNode docs = root.get("documentosSolicitados");
        if (docs == null || !docs.isArray() || docs.size() == 0) {
            throw new IllegalArgumentException("Se requiere al menos un documento solicitado");
        }
        if (root.get("contratante") == null || root.get("contratante").isNull()) {
            throw new IllegalArgumentException("Contratante es obligatorio");
        }
    }

    // =====================================================================
    // Construcción del payload EXACTO que el SP espera, sin getters del DTO
    // =====================================================================
    private String buildSpPayload(IngresoSolicitudRequestDto request) throws Exception {
        JsonNode src = mapper.valueToTree(request); // árbol del DTO real
        ObjectNode root = mapper.createObjectNode();

        // ---------- Escalares ----------
        copyIfPresent(src, root, "idRubro");
        copyIfPresent(src, root, "idProducto");
        copyIfPresent(src, root, "clausula");
        copyIfPresent(src, root, "observacion");
        copyIfPresent(src, root, "estado");
        // materiaAsegurada puede ser objeto/string: copiamos tal cual
        if (src.has("materiaAsegurada")) {
            root.set("materiaAsegurada", src.get("materiaAsegurada"));
        }

        // ---------- Contratante ----------
        ObjectNode c = root.putObject("contratante");
        JsonNode con = src.get("contratante");
        if (con != null && con.isObject()) {
            copyIfPresent(con, c, "rut");
            copyIfPresent(con, c, "nombre");
            copyIfPresent(con, c, "apellidoPaterno");
            copyIfPresent(con, c, "apellidoMaterno");
            copyIfPresent(con, c, "region");
            copyIfPresent(con, c, "ciudad");
            copyIfPresent(con, c, "comuna");
            copyIfPresent(con, c, "calle");
            copyIfPresent(con, c, "numero");
            copyIfPresent(con, c, "casa");
            // Mapeo clave SP: deptoBlock (acepta "departamento" o ya "deptoBlock")
            String depto = textOrNull(con, "deptoBlock");
            if (depto == null) depto = textOrNull(con, "departamento");
            if (depto != null) c.put("deptoBlock", depto);
        }

        // ---------- Asegurados ----------
        ArrayNode asegOut = root.putArray("asegurados");
        JsonNode asegIn = src.get("asegurados");
        if (asegIn != null && asegIn.isArray()) {
            for (JsonNode a : asegIn) {
                if (a == null || !a.isObject()) continue;
                ObjectNode ao = mapper.createObjectNode();
                copyIfPresent(a, ao, "rut");
                copyIfPresent(a, ao, "nombre");
                copyIfPresent(a, ao, "apellidoPaterno");
                copyIfPresent(a, ao, "apellidoMaterno");
                copyIfPresent(a, ao, "region");
                copyIfPresent(a, ao, "ciudad");
                copyIfPresent(a, ao, "comuna");
                copyIfPresent(a, ao, "calle");
                copyIfPresent(a, ao, "numero");
                copyIfPresent(a, ao, "casa");
                String depto = textOrNull(a, "deptoBlock");
                if (depto == null) depto = textOrNull(a, "departamento");
                if (depto != null) ao.put("deptoBlock", depto);
                asegOut.add(ao);
            }
        }

        // ---------- Beneficiarios ----------
        ArrayNode beneOut = root.putArray("beneficiarios");
        JsonNode beneIn = src.get("beneficiarios");
        if (beneIn != null && beneIn.isArray()) {
            for (JsonNode b : beneIn) {
                if (b == null || !b.isObject()) continue;
                ObjectNode bo = mapper.createObjectNode();
                copyIfPresent(b, bo, "rut");
                copyIfPresent(b, bo, "nombre");
                copyIfPresent(b, bo, "apellidoPaterno");
                copyIfPresent(b, bo, "apellidoMaterno");
                copyIfPresent(b, bo, "region");
                copyIfPresent(b, bo, "ciudad");
                copyIfPresent(b, bo, "comuna");
                copyIfPresent(b, bo, "calle");
                copyIfPresent(b, bo, "numero");
                String depto = textOrNull(b, "deptoBlock");
                if (depto == null) depto = textOrNull(b, "departamento");
                if (depto != null) bo.put("deptoBlock", depto);
                beneOut.add(bo);
            }
        }

        // ---------- Documentos ----------
        ArrayNode docsOut = root.putArray("documentosSolicitados");
        JsonNode docsIn = src.get("documentosSolicitados");
        if (docsIn != null && docsIn.isArray()) {
            for (JsonNode d : docsIn) {
                if (d == null || !d.isObject()) continue;
                ObjectNode dn = mapper.createObjectNode();
                // nombreArchivo/rutaArchivo (acepta nombre/ruta como alias)
                String nombre = textOrNull(d, "nombreArchivo");
                if (nombre == null) nombre = textOrNull(d, "nombre");
                String ruta = textOrNull(d, "rutaArchivo");
                if (ruta == null) ruta = textOrNull(d, "ruta");
                if (nombre != null) dn.put("nombreArchivo", nombre);
                if (ruta != null) dn.put("rutaArchivo", normalizeWinPath(ruta));
                docsOut.add(dn);
            }
        }

        // ---------- Cuestionario ----------
        ObjectNode qOut = root.putObject("cuestionario");
        JsonNode qIn = src.get("cuestionario");
        if (qIn != null && qIn.isObject()) {
            String nombre = textOrNull(qIn, "nombreArchivo");
            if (nombre == null) nombre = textOrNull(qIn, "nombre");
            String ruta = textOrNull(qIn, "rutaArchivo");
            if (ruta == null) ruta = textOrNull(qIn, "ruta");
            if (nombre != null) qOut.put("nombreArchivo", nombre);
            if (ruta != null) qOut.put("rutaArchivo", normalizeWinPath(ruta));
        }

        String json = mapper.writeValueAsString(root);
        if (LOGGER.isDebugEnabled()) {
            // Doble-escape solo para ver legible en logs
            LOGGER.debug("Payload SP (preview) = {}", json.replace("\\", "\\\\"));
        }
        return json;
    }

    // ================== Utilidades ==================

    private static void copyIfPresent(JsonNode from, ObjectNode to, String field) {
        if (from.has(field) && !from.get(field).isNull()) {
            to.set(field, from.get(field));
        }
    }

    private static String textOrNull(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return (v != null && !v.isNull()) ? v.asText() : null;
    }

    /** Normaliza rutas Windows: "C:\\temp\\a.pdf" -> "C:\temp\a.pdf" (no toca UNC) */
    private static String normalizeWinPath(String p) {
        if (p == null) return null;
        if (p.startsWith("\\\\")) return p; // UNC
        return p.replace("\\\\", "\\");
    }
}
