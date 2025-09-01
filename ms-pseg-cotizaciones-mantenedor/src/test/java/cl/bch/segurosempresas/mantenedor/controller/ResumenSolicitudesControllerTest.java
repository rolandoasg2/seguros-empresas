package cl.bch.segurosempresas.mantenedor.controller;

import cl.bch.segurosempresas.mantenedor.dto.ResumenSolicitudesRequestDto;
import cl.bch.segurosempresas.mantenedor.dto.ResumenSolicitudesResponseDto;
import cl.bch.segurosempresas.mantenedor.service.ResumenSolicitudesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ResumenSolicitudesControllerTest {

    private ResumenSolicitudesService service;
    private ResumenSolicitudesController controller;

    @BeforeEach
    void setUp() {
        service = mock(ResumenSolicitudesService.class);
        controller = new ResumenSolicitudesController(service);
    }

    @Test
    void obtenerResumen_deberiaResponder200_ok() {
        // given
        ResumenSolicitudesRequestDto request = new ResumenSolicitudesRequestDto();
        ResumenSolicitudesResponseDto resp = new ResumenSolicitudesResponseDto();
        resp.setCodigo(0);
        resp.setEstado("OK");
        resp.setEstadoCreacion("OK");
        resp.setMensaje("Consulta exitosa");

        when(service.obtenerResumenSolicitudes(ArgumentMatchers.any())).thenReturn(resp);

        // when
        ResponseEntity<ResumenSolicitudesResponseDto> response = controller.obtenerResumen(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEstado()).isEqualTo("OK");
        assertThat(response.getBody().getCodigo()).isEqualTo(0);

        verify(service, times(1)).obtenerResumenSolicitudes(any());
    }

    @Test
    void obtenerResumen_deberiaResponder400_cuandoServiceLanzaIllegalArgumentException() {
        // given
        ResumenSolicitudesRequestDto request = new ResumenSolicitudesRequestDto();
        when(service.obtenerResumenSolicitudes(any()))
                .thenThrow(new IllegalArgumentException("Parámetros inválidos"));

        // when
        ResponseEntity<ResumenSolicitudesResponseDto> response = controller.obtenerResumen(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEstado()).isEqualTo("ERROR_VALIDACION");
        assertThat(response.getBody().getEstadoCreacion()).isEqualTo("ERROR");
        assertThat(response.getBody().getCodigo()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getBody().getMensaje()).isEqualTo("Parámetros inválidos");

        verify(service, times(1)).obtenerResumenSolicitudes(any());
    }

    @Test
    void obtenerResumen_deberiaResponder500_cuandoOcurreRuntimeException() {
        // given
        ResumenSolicitudesRequestDto request = new ResumenSolicitudesRequestDto();
        when(service.obtenerResumenSolicitudes(any()))
                .thenThrow(new RuntimeException("Fallo inesperado"));

        // when
        ResponseEntity<ResumenSolicitudesResponseDto> response = controller.obtenerResumen(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEstado()).isEqualTo("ERROR");
        assertThat(response.getBody().getEstadoCreacion()).isEqualTo("ERROR");
        assertThat(response.getBody().getCodigo()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getBody().getMensaje()).isEqualTo("Error al procesar la solicitud");

        verify(service, times(1)).obtenerResumenSolicitudes(any());
    }
}
