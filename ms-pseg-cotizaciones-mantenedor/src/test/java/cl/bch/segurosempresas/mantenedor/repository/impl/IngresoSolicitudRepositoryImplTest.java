package cl.bch.segurosempresas.mantenedor.repository.impl;

import cl.bch.segurosempresas.mantenedor.dto.IngresoSolicitudResponseDto;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class IngresoSolicitudRepositoryImplTest {

    @Test
    void ejecutarSpIngresoSolicitud_ok_mapeaOutputs() {
        JdbcTemplate jdbc = mock(JdbcTemplate.class);

        try (MockedConstruction<SimpleJdbcCall> mocked = mockConstruction(SimpleJdbcCall.class, (mock, ctx) -> {
            // encadenamiento fluido
            when(mock.withoutProcedureColumnMetaDataAccess()).thenReturn(mock);
            when(mock.withProcedureName(anyString())).thenReturn(mock);
            // IMPORTANTE: stub de VARARGS (4 parámetros en tu repo)
            when(mock.declareParameters(
                    any(SqlParameter.class),
                    any(SqlParameter.class),
                    any(SqlParameter.class),
                    any(SqlParameter.class)
            )).thenReturn(mock);

            Map<String,Object> out = new HashMap<>();
            out.put("p_id_solicitud", "123");
            out.put("vcEstado", "OK");
            out.put("vcEstadoCreacion", "OK");
            when(mock.execute(any(MapSqlParameterSource.class))).thenReturn(out);
        })) {
            IngresoSolicitudRepositoryImpl repo = new IngresoSolicitudRepositoryImpl(jdbc);

            IngresoSolicitudResponseDto resp = repo.ejecutarSpIngresoSolicitud("{\"x\":1}");

            assertThat(resp.getEstado()).isEqualTo("OK");
            assertThat(resp.getEstadoCreacion()).isEqualTo("OK");
            assertThat(resp.getIdSolicitud()).isEqualTo(123L);
            assertThat(resp.getCodigo()).isEqualTo(0);
            assertThat(resp.getMensaje()).contains("Solicitud creada");
        }
    }

    @Test
    void ejecutarSpIngresoSolicitud_error_mapeaMensaje() {
        JdbcTemplate jdbc = mock(JdbcTemplate.class);

        try (MockedConstruction<SimpleJdbcCall> mocked = mockConstruction(SimpleJdbcCall.class, (mock, ctx) -> {
            when(mock.withoutProcedureColumnMetaDataAccess()).thenReturn(mock);
            when(mock.withProcedureName(anyString())).thenReturn(mock);
            when(mock.declareParameters(
                    any(SqlParameter.class),
                    any(SqlParameter.class),
                    any(SqlParameter.class),
                    any(SqlParameter.class)
            )).thenReturn(mock);

            Map<String,Object> out = new HashMap<>();
            out.put("p_id_solicitud", null);
            out.put("vcEstado", "ERROR");
            out.put("vcEstadoCreacion", "Error: dato inválido");
            when(mock.execute(any(MapSqlParameterSource.class))).thenReturn(out);
        })) {
            IngresoSolicitudRepositoryImpl repo = new IngresoSolicitudRepositoryImpl(jdbc);

            IngresoSolicitudResponseDto resp = repo.ejecutarSpIngresoSolicitud("{\"x\":1}");

            assertThat(resp.getEstado()).isEqualTo("ERROR");
            assertThat(resp.getEstadoCreacion()).isEqualTo("Error: dato inválido");
            assertThat(resp.getIdSolicitud()).isNull();
            assertThat(resp.getCodigo()).isEqualTo(1);
            assertThat(resp.getMensaje()).contains("Error");
        }
    }
}
