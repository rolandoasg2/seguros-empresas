package cl.bch.segurosempresas.mantenedor.repository.impl;

import cl.bch.segurosempresas.mantenedor.dto.IngresoSolicitudResponseDto;
import cl.bch.segurosempresas.mantenedor.repository.IngresoSolicitudRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.Map;

@Repository
public class IngresoSolicitudRepositoryImpl implements IngresoSolicitudRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(IngresoSolicitudRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;

    public IngresoSolicitudRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public IngresoSolicitudResponseDto ejecutarSpIngresoSolicitud(String payloadJson) {
        // Ajusta schema si aplica: .withSchemaName("SCHEMA_BCH")
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withoutProcedureColumnMetaDataAccess()
                .withProcedureName("spCSEingresoSolicitud_rolo")
                .declareParameters(
                        // NOMBRES EXACTOS, tal como en el SP
                        new SqlParameter("p_json", Types.CLOB),
                        new SqlOutParameter("p_id_solicitud", Types.VARCHAR),
                        new SqlOutParameter("vcEstado", Types.VARCHAR),
                        new SqlOutParameter("vcEstadoCreacion", Types.VARCHAR)
                );

        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("p_json", payloadJson, Types.CLOB); // nombre exacto

        Map<String, Object> out = call.execute(in);

        String idSolicitudStr = (String) out.get("p_id_solicitud");
        String vcEstado = (String) out.get("vcEstado");
        String vcEstadoCreacion = (String) out.get("vcEstadoCreacion");

        IngresoSolicitudResponseDto resp = new IngresoSolicitudResponseDto();
        resp.setEstado(vcEstado != null ? vcEstado : "ERROR");
        resp.setEstadoCreacion(vcEstadoCreacion != null ? vcEstadoCreacion : "ERROR");

        if (idSolicitudStr != null && !idSolicitudStr.isEmpty()) {
            try {
                resp.setIdSolicitud(Long.parseLong(idSolicitudStr));
            } catch (NumberFormatException nfe) {
                // Lo deja null si no se puede convertir; no es crítico
                LOGGER.warn("p_id_solicitud no es numérico: '{}'", idSolicitudStr);
            }
        }

        if ("OK".equalsIgnoreCase(resp.getEstado())) {
            resp.setCodigo(0);
            resp.setMensaje("Solicitud creada correctamente");
        } else {
            resp.setCodigo(1);
            // Cuando el SP manda mensaje de error en vcEstadoCreacion lo reflejamos
            resp.setMensaje(vcEstadoCreacion != null ? vcEstadoCreacion : "Error al crear solicitud");
        }

        LOGGER.info("SP spCSEingresoSolicitud_rolo ejecutado: estado={}, estadoCreacion={}, id={}",
                resp.getEstado(), resp.getEstadoCreacion(), resp.getIdSolicitud());

        return resp;
    }
}
