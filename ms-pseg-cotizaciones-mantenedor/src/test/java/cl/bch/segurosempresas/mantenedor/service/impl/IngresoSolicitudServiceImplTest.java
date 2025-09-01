package cl.bch.segurosempresas.mantenedor.service.impl;

import cl.bch.segurosempresas.mantenedor.dto.IngresoSolicitudRequestDto;
import cl.bch.segurosempresas.mantenedor.dto.IngresoSolicitudRequestDto.CuestionarioDto;
import cl.bch.segurosempresas.mantenedor.dto.IngresoSolicitudRequestDto.DocumentoDto;
import cl.bch.segurosempresas.mantenedor.dto.IngresoSolicitudRequestDto.PersonaDto;
import cl.bch.segurosempresas.mantenedor.dto.IngresoSolicitudResponseDto;
import cl.bch.segurosempresas.mantenedor.repository.IngresoSolicitudRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class IngresoSolicitudServiceImplTest {

    private IngresoSolicitudRepository repository;
    private IngresoSolicitudServiceImpl service;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        repository = mock(IngresoSolicitudRepository.class);
        service = new IngresoSolicitudServiceImpl(repository);
    }

    @Test
    void ingresarSolicitud_ok_mapeaPayloadAlSP_yDevuelveOK() throws Exception {
        // Request “lado API” (con alias nombre/ruta y departamento)
        IngresoSolicitudRequestDto req = new IngresoSolicitudRequestDto();
        req.setIdRubro(99);
        req.setIdProducto(1234);
        req.setEstado("En revisión");

        PersonaDto c = new PersonaDto();
        c.setRut("9.876.543-2");
        c.setNombre("MARIA");
        c.setApellidoPaterno("ROJAS");
        c.setApellidoMaterno("LOPEZ");
        c.setDepartamento("T2"); // alias -> debe mapearse a deptoBlock
        req.setContratante(c);

        DocumentoDto d1 = new DocumentoDto(); d1.setNombre("doc1.pdf"); d1.setRuta("C:\\temp\\doc1.pdf");
        DocumentoDto d2 = new DocumentoDto(); d2.setNombre("doc2.jpg"); d2.setRuta("C:\\temp\\doc2.jpg");
        req.setDocumentosSolicitados(List.of(d1, d2));

        CuestionarioDto q = new CuestionarioDto(); q.setNombre("q.pdf"); q.setRuta("C:\\temp\\q.pdf");
        req.setCuestionario(q);

        IngresoSolicitudResponseDto ok = new IngresoSolicitudResponseDto();
        ok.setEstado("OK"); ok.setEstadoCreacion("OK"); ok.setCodigo(0); ok.setIdSolicitud(777L);

        ArgumentCaptor<String> payload = ArgumentCaptor.forClass(String.class);
        when(repository.ejecutarSpIngresoSolicitud(payload.capture())).thenReturn(ok);

        // Act
        IngresoSolicitudResponseDto resp = service.ingresarSolicitud(req);

        // Assert respuesta
        assertThat(resp.getEstado()).isEqualTo("OK");
        assertThat(resp.getIdSolicitud()).isEqualTo(777L);

        // Assert payload para SP
        String json = payload.getValue();
        JsonNode root = mapper.readTree(json);
        assertThat(root.at("/contratante/deptoBlock").asText()).isEqualTo("T2");
        assertThat(root.at("/documentosSolicitados/0/nombreArchivo").asText()).isEqualTo("doc1.pdf");
        assertThat(root.at("/documentosSolicitados/0/rutaArchivo").asText()).endsWith("\\temp\\doc1.pdf");
        assertThat(root.at("/cuestionario/nombreArchivo").asText()).isEqualTo("q.pdf");
        assertThat(root.at("/cuestionario/rutaArchivo").asText()).endsWith("\\temp\\q.pdf");

        verify(repository, times(1)).ejecutarSpIngresoSolicitud(anyString());
    }

    @Test
    void ingresarSolicitud_sinDocumentos_lanzaIllegalArgumentException() {
        IngresoSolicitudRequestDto req = new IngresoSolicitudRequestDto();
        CuestionarioDto q = new CuestionarioDto(); q.setNombre("q.pdf"); q.setRuta("C:\\temp\\q.pdf");
        req.setCuestionario(q);
        req.setDocumentosSolicitados(null); // inválido

        assertThatThrownBy(() -> service.ingresarSolicitud(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("al menos un documento");
        verifyNoInteractions(repository);
    }

    @Test
    void ingresarSolicitud_repoDevuelveERROR_lanzaIllegalState() {
        IngresoSolicitudRequestDto req = new IngresoSolicitudRequestDto();

        DocumentoDto d = new DocumentoDto(); d.setNombre("a"); d.setRuta("b");
        req.setDocumentosSolicitados(List.of(d));
        CuestionarioDto q = new CuestionarioDto(); q.setNombre("q"); q.setRuta("r");
        req.setCuestionario(q);
        PersonaDto c = new PersonaDto(); c.setRut("1"); c.setNombre("N");
        req.setContratante(c);

        IngresoSolicitudResponseDto err = new IngresoSolicitudResponseDto();
        err.setEstado("ERROR"); err.setEstadoCreacion("Error: X"); err.setCodigo(1);

        when(repository.ejecutarSpIngresoSolicitud(anyString())).thenReturn(err);

        assertThatThrownBy(() -> service.ingresarSolicitud(req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Error interno al registrar la solicitud");
    }
}
