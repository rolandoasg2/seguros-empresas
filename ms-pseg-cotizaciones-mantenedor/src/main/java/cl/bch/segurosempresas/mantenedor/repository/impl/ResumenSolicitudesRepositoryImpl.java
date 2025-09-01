package cl.bch.segurosempresas.mantenedor.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cl.bch.segurosempresas.mantenedor.dto.ResumenSolicitudDto;
import cl.bch.segurosempresas.mantenedor.dto.ResumenSolicitudesResponseDto;
import cl.bch.segurosempresas.mantenedor.repository.ResumenSolicitudesRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Repository: ejecución de Stored Procedures Oracle para el resumen.
 * - Selecciona el SP por rol (EJE / SUP / COOR).
 * - Lee SOLO los campos del cursor (8 columnas) y los traspasa al DTO.
 * - NO calcula ni “cuenta” totales aquí.
 * - Ante error, lanza RuntimeException (el controller lo mapea a 500/400 según tu manejo).
 */
@Repository
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ResumenSolicitudesRepositoryImpl implements ResumenSolicitudesRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResumenSolicitudesRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    // Nombres de parámetros (coinciden con tus SP)
    private static final String P_ID_USUARIO      = "P_IdUsuario";
    private static final String P_FECHA           = "P_Fecha";
    private static final String P_ROL             = "P_Rol";
    private static final String P_CURSOR          = "datosSolicitud";
    private static final String P_ESTADO          = "vcEstado";
    private static final String P_ESTADO_CREACION = "vcEstadoCreacion";

    // Solo existen en el SP de EJE (si tus SP SUP/COOR no los tienen, no se registran)
    private static final String P_EN_PROCESO      = "totalSolicitudesEnProceso";
    private static final String P_ESPERANDO       = "totalSolicitudesEsperandoRespuesta";
    private static final String P_APROBADAS       = "totalCotizacionesAprobadas";
    private static final String P_CON_OBS         = "totalSolicitudesConObservaciones";

    @Override
    public ResumenSolicitudesResponseDto findSolicitudesByUsuarioAndFechaAndRol(
            final Integer idUsuario, final String fecha, final String rol) {

        final String spName = resolveSpName(rol);
        final boolean spEsEje = "spCSEResumenSolicitudesEje_rolo".equalsIgnoreCase(spName);

        try {
            // Armar el StoredProcedureQuery con firma correcta según el SP elegido
            StoredProcedureQuery q = entityManager
                    .createStoredProcedureQuery(spName)
                    .registerStoredProcedureParameter(P_ID_USUARIO, Integer.class, ParameterMode.IN)
                    .registerStoredProcedureParameter(P_FECHA, Date.class, ParameterMode.IN)
                    .registerStoredProcedureParameter(P_ROL, String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter(P_CURSOR, void.class, ParameterMode.REF_CURSOR);

            if (spEsEje) {
                // Solo EJE: estos OUT existen
                q.registerStoredProcedureParameter(P_EN_PROCESO, Integer.class, ParameterMode.OUT)
                 .registerStoredProcedureParameter(P_ESPERANDO, Integer.class, ParameterMode.OUT)
                 .registerStoredProcedureParameter(P_APROBADAS, Integer.class, ParameterMode.OUT)
                 .registerStoredProcedureParameter(P_CON_OBS, Integer.class, ParameterMode.OUT);
            }

            q.registerStoredProcedureParameter(P_ESTADO, String.class, ParameterMode.OUT)
             .registerStoredProcedureParameter(P_ESTADO_CREACION, String.class, ParameterMode.OUT);

            q.setParameter(P_ID_USUARIO, idUsuario);
            q.setParameter(P_FECHA, toSqlDate(fecha));
            q.setParameter(P_ROL, rol);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Ejecutando {} P_IdUsuario={} P_Fecha='{}' P_Rol={}", spName, idUsuario, fecha, rol);
            }

            q.execute();

            // Estados del SP
            final String estadoSP         = asString(q.getOutputParameterValue(P_ESTADO));
            final String estadoCreacionSP = asString(q.getOutputParameterValue(P_ESTADO_CREACION));

            // Resultado principal (cursor) — esperamos 8 columnas en orden:
            // 1 idSolicitud, 2 fechaCreacion, 3 nombreContratante, 4 rubro,
            // 5 tipoSeguro, 6 coordinador, 7 ejecutivo, 8 estadoSolicitud
            @SuppressWarnings("unchecked")
            final List<Object[]> rows = q.getResultList();
            final List<ResumenSolicitudDto> items = new ArrayList<>();

            if (rows != null) {
                for (Object[] r : rows) {
                    final ResumenSolicitudDto dto = new ResumenSolicitudDto();
                    dto.setIdSolicitud(col(r, 0));
                    dto.setFechaCreacion(col(r, 1));
                    dto.setNombreContratante(col(r, 2));
                    dto.setRubro(col(r, 3));
                    dto.setTipoSeguro(col(r, 4));
                    dto.setCoordinador(col(r, 5));
                    dto.setEjecutivo(col(r, 6));
                    dto.setEstadoSolicitud(col(r, 7));
                    items.add(dto);
                }
            }

            // Armar respuesta. Aquí NO seteamos 'codigo' (lo hace el Service).
            final ResumenSolicitudesResponseDto resp = new ResumenSolicitudesResponseDto();
            resp.setEstado(estadoSP != null ? estadoSP : "OK");
            resp.setEstadoCreacion(estadoCreacionSP != null ? estadoCreacionSP : "OK");
            resp.setMensaje("Resumen recuperado correctamente");
            resp.setItems(items);
            resp.setCantidad(items.size());

            // Si el SP EJE trae OUT de totales, los copiamos tal cual; si no, los dejamos en 0.
            if (spEsEje) {
                resp.setTotalPendientes(nvlInt(q.getOutputParameterValue(P_EN_PROCESO)));
                // Mapea estos si tu contrato de respuesta los usa;
                // si no corresponde, puedes dejarlos en 0.
                resp.setTotalAprobadas(nvlInt(q.getOutputParameterValue(P_APROBADAS)));
                resp.setTotalRechazadas(0); // no viene desde SP; ajusta si aplica
            } else {
                // SUP/COOR: si esos totales no vienen por OUT, los dejas en 0
                resp.setTotalPendientes(0);
                resp.setTotalAprobadas(0);
                resp.setTotalRechazadas(0);
            }

            return resp;

        } catch (IllegalArgumentException iae) {
            // rol no soportado (flujo de validación); lo dejamos subir
            throw iae;
        } catch (Exception e) {
            // Lanzamos RuntimeException como pediste; el Controller la captura y responde 500/400
            LOGGER.error("Error ejecutando SP {} (idUsuario={}, fecha='{}', rol={})",
                    spName, idUsuario, fecha, rol, e);
            throw new RuntimeException("Error al obtener resumen de solicitudes: " + e.getMessage(), e);
        }
    }

    /** Selecciona el nombre del SP según rol (si no coincide, lanza IllegalArgumentException). */
    private String resolveSpName(final String rolRaw) {
        final String rol = (rolRaw == null) ? "" : rolRaw.trim().toUpperCase(Locale.ROOT);
        switch (rol) {
            case "EJE":
            case "EJECUTIVO":
                return "spCSEResumenSolicitudesEje_rolo";
            case "SUP":
            case "SUPERVISOR":
                return "spCSEResumenSolicitudesSup_rolo";
            case "COOR":
            case "COORDINADOR":
                return "spCSEResumenSolicitudesCoor_rolo";
            default:
                throw new IllegalArgumentException("Rol no soportado. Use EJE, SUP o COOR.");
        }
    }

    // ===== Helpers =====

    private static Date toSqlDate(final String f) {
        if (f == null || f.isBlank()) return null;
        final String v = f.trim();
        final DateTimeFormatter[] fmts = new DateTimeFormatter[] {
                DateTimeFormatter.ISO_LOCAL_DATE,               // yyyy-MM-dd
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("yyyyMMdd")
        };
        for (DateTimeFormatter fmt : fmts) {
            try { return Date.valueOf(LocalDate.parse(v, fmt)); }
            catch (DateTimeParseException ignore) { /* probar siguiente formato */ }
        }
        return null;
    }

    private static String asString(final Object o) { return (o == null) ? null : o.toString(); }

    private static String col(final Object[] row, final int idx) {
        if (row == null || idx < 0 || idx >= row.length) return null;
        final Object v = row[idx];
        return (v == null) ? null : v.toString();
    }

    private static int nvlInt(final Object v) {
        if (v == null) return 0;
        if (v instanceof Number n) return n.intValue();
        try { return Integer.parseInt(v.toString()); } catch (NumberFormatException ex) { return 0; }
    }
}
