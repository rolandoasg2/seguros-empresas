package cl.bch.segurosempresas.mantenedor.service.impl;

import cl.bch.segurosempresas.mantenedor.dto.ResumenSolicitudesRequestDto;
import cl.bch.segurosempresas.mantenedor.dto.ResumenSolicitudesResponseDto;
import cl.bch.segurosempresas.mantenedor.repository.ResumenSolicitudesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ResumenSolicitudesServiceImplTest {

    private ResumenSolicitudesRepository repository;
    private ResumenSolicitudesServiceImpl service;

    @BeforeEach
    void setup() {
        repository = mock(ResumenSolicitudesRepository.class);
        service = new ResumenSolicitudesServiceImpl(repository);
    }

    @Test
    void obtenerResumenSolicitudes_ok_setsCodigo0_y_delegaParametros() {
        ResumenSolicitudesRequestDto req = new ResumenSolicitudesRequestDto();
        req.setIdUsuario(42);
        req.setFecha("2025-08-29");
        req.setRol("EJE");

        ResumenSolicitudesResponseDto repoResp = new ResumenSolicitudesResponseDto();
        repoResp.setEstado("OK");
        repoResp.setEstadoCreacion("OK");
        repoResp.setMensaje("OK");

        ArgumentCaptor<Integer> idCap = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> fCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> rolCap = ArgumentCaptor.forClass(String.class);

        when(repository.findSolicitudesByUsuarioAndFechaAndRol(idCap.capture(), fCap.capture(), rolCap.capture()))
                .thenReturn(repoResp);

        ResumenSolicitudesResponseDto out = service.obtenerResumenSolicitudes(req);

        assertThat(out.getCodigo()).isEqualTo(0);
        assertThat(out.getEstado()).isEqualTo("OK");
        assertThat(idCap.getValue()).isEqualTo(42);
        assertThat(fCap.getValue()).isEqualTo("2025-08-29");
        assertThat(rolCap.getValue()).isEqualTo("EJE");
    }

    @Test
    void obtenerResumenSolicitudes_nonOk_setsCodigo1_yMensajePorDefectoSiNull() {
        ResumenSolicitudesRequestDto req = new ResumenSolicitudesRequestDto();

        ResumenSolicitudesResponseDto repoResp = new ResumenSolicitudesResponseDto();
        repoResp.setEstado("ERROR");
        repoResp.setEstadoCreacion("OK");
        repoResp.setMensaje(null);

        when(repository.findSolicitudesByUsuarioAndFechaAndRol(any(), any(), any())).thenReturn(repoResp);

        ResumenSolicitudesResponseDto out = service.obtenerResumenSolicitudes(req);

        assertThat(out.getCodigo()).isEqualTo(1);
        assertThat(out.getMensaje()).isNotBlank();
    }
}
