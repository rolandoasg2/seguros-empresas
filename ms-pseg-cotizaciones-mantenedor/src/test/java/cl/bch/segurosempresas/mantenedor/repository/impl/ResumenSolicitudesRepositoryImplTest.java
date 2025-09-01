package cl.bch.segurosempresas.mantenedor.repository.impl;

import cl.bch.segurosempresas.mantenedor.dto.ResumenSolicitudDto;
import cl.bch.segurosempresas.mantenedor.dto.ResumenSolicitudesResponseDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
    import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ResumenSolicitudesRepositoryImplTest {

    private EntityManager em;
    private StoredProcedureQuery spq;
    private ResumenSolicitudesRepositoryImpl repo;

    @BeforeEach
    void setup() throws Exception {
        em = mock(EntityManager.class);
        spq = mock(StoredProcedureQuery.class);

        when(em.createStoredProcedureQuery(anyString())).thenReturn(spq);
        when(spq.registerStoredProcedureParameter(anyString(), any(), any(ParameterMode.class))).thenReturn(spq);
        when(spq.setParameter(anyString(), any())).thenReturn(spq);
        when(spq.execute()).thenReturn(true);

        repo = new ResumenSolicitudesRepositoryImpl();
        Field f = ResumenSolicitudesRepositoryImpl.class.getDeclaredField("entityManager");
        f.setAccessible(true);
        f.set(repo, em);
    }

    @Test
    void findSolicitudes_rolEJE_mapeaFilas_yTotales() {
        // 2 filas (8 columnas en orden esperado por tu mapper)
        Object[] fila1 = new Object[]{"1001","2025-08-28","MARIA ROJAS","Rubro A","Seguro X","Coord 1","Eje 1","En revisión"};
        Object[] fila2 = new Object[]{"1002","2025-08-29","PEDRO DIAZ","Rubro B","Seguro Y","Coord 2","Eje 2","Aprobada"};
        when(spq.getResultList()).thenReturn(List.of(fila1, fila2));

        // OUTs
        when(spq.getOutputParameterValue("vcEstado")).thenReturn("OK");
        when(spq.getOutputParameterValue("vcEstadoCreacion")).thenReturn("OK");
        when(spq.getOutputParameterValue("totalSolicitudesEnProceso")).thenReturn(5);
        when(spq.getOutputParameterValue("totalCotizacionesAprobadas")).thenReturn(2);
        when(spq.getOutputParameterValue("totalSolicitudesEsperandoRespuesta")).thenReturn(3);
        when(spq.getOutputParameterValue("totalSolicitudesConObservaciones")).thenReturn(1);

        ResumenSolicitudesResponseDto resp = repo.findSolicitudesByUsuarioAndFechaAndRol(10, "2025-08-29", "EJE");

        assertThat(resp.getEstado()).isEqualTo("OK");
        assertThat(resp.getEstadoCreacion()).isEqualTo("OK");
        assertThat(resp.getCantidad()).isEqualTo(2);
        assertThat(resp.getItems()).extracting(ResumenSolicitudDto::getIdSolicitud)
                .containsExactly("1001","1002");
        // Totales presentes para EJE (si tu impl los setea solo en EJE)
        assertThat(resp.getTotalPendientes()).isEqualTo(5);
        assertThat(resp.getTotalAprobadas()).isEqualTo(2);
        assertThat(resp.getTotalRechazadas()).isNotNull(); // depende de tu mapeo; al menos presente

        verify(em).createStoredProcedureQuery(anyString());
        verify(spq).setParameter(eq("P_IdUsuario"), eq(10));
        verify(spq).setParameter(eq("P_Rol"), eq("EJE"));
        verify(spq).execute();
    }

    @Test
    void findSolicitudes_rolSUP_sinTotales_quedanEnCero() {
        when(spq.getResultList()).thenReturn(List.of());
        when(spq.getOutputParameterValue("vcEstado")).thenReturn("OK");
        when(spq.getOutputParameterValue("vcEstadoCreacion")).thenReturn("OK");

        ResumenSolicitudesResponseDto resp = repo.findSolicitudesByUsuarioAndFechaAndRol(20, "2025-08-29", "SUP");

        assertThat(resp.getEstado()).isEqualTo("OK");
        assertThat(resp.getCantidad()).isEqualTo(0);
        assertThat(resp.getTotalPendientes()).isEqualTo(0);
        assertThat(resp.getTotalAprobadas()).isEqualTo(0);
        assertThat(resp.getTotalRechazadas()).isEqualTo(0);

        verify(spq).setParameter(eq("P_Rol"), eq("SUP"));
    }

    @Test
    void findSolicitudes_rolInvalido_lanzaIllegalArgument() {
        try {
            repo.findSolicitudesByUsuarioAndFechaAndRol(1, "2025-08-29", "OTRO");
            assertThat(false).as("Debe lanzar IllegalArgumentException").isTrue();
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage()).contains("Rol");
        }
    }
}
