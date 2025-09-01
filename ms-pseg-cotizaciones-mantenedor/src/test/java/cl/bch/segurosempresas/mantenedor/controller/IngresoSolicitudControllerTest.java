package cl.bch.segurosempresas.mantenedor.controller;

import cl.bch.segurosempresas.mantenedor.dto.IngresoSolicitudRequestDto;
import cl.bch.segurosempresas.mantenedor.dto.IngresoSolicitudResponseDto;
import cl.bch.segurosempresas.mantenedor.service.IngresoSolicitudService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class IngresoSolicitudControllerTest {

    private IngresoSolicitudService service;
    private IngresoSolicitudController controller;

    @BeforeEach
    void setUp() {
        service = mock(IngresoSolicitudService.class);
        controller = new IngresoSolicitudController(service);
    }

    @Test
    void ingresoSolicitud_deberiaResponder200_ok() {
        // given
        IngresoSolicitudRequestDto request = new IngresoSolicitudRequestDto();
        IngresoSolicitudResponseDto resp = new IngresoSolicitudResponseDto();
        resp.setCodigo(0);
        resp.setEstado("OK");
        resp.setEstadoCreacion("CREADA");
        resp.setMensaje("Solicitud creada correctamente");
        resp.setIdSolicitud(12345L);

        when(service.ingresarSolicitud(ArgumentMatchers.any())).thenReturn(resp);

        // when
        ResponseEntity<IngresoSolicitudResponseDto> response = controller.ingresoSolicitud(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEstado()).isEqualTo("OK");
        assertThat(response.getBody().getCodigo()).isEqualTo(0);
        assertThat(response.getBody().getIdSolicitud()).isEqualTo(12345L);

        verify(service, times(1)).ingresarSolicitud(any());
    }

    @Test
    void ingresoSolicitud_deberiaResponder400_cuandoServiceLanzaIllegalArgumentException() {
        // given
        IngresoSolicitudRequestDto request = new IngresoSolicitudRequestDto();
        when(service.ingresarSolicitud(any()))
                .thenThrow(new IllegalArgumentException("Faltan campos obligatorios"));

        // when
        ResponseEntity<IngresoSolicitudResponseDto> response = controller.ingresoSolicitud(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEstado()).isEqualTo("ERROR_VALIDACION");
        assertThat(response.getBody().getEstadoCreacion()).isEqualTo("ERROR");
        assertThat(response.getBody().getCodigo()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getBody().getMensaje()).isEqualTo("Faltan campos obligatorios");

        verify(service, times(1)).ingresarSolicitud(any());
    }

    @Test
    void ingresoSolicitud_deberiaResponder500_cuandoOcurreRuntimeException() {
        // given
        IngresoSolicitudRequestDto request = new IngresoSolicitudRequestDto();
        when(service.ingresarSolicitud(any()))
                .thenThrow(new RuntimeException("Fallo inesperado"));

        // when
        ResponseEntity<IngresoSolicitudResponseDto> response = controller.ingresoSolicitud(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEstado()).isEqualTo("ERROR");
        assertThat(response.getBody().getEstadoCreacion()).isEqualTo("ERROR");
        assertThat(response.getBody().getCodigo()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getBody().getMensaje()).isEqualTo("Error al procesar la solicitud");

        verify(service, times(1)).ingresarSolicitud(any());
    }
}
