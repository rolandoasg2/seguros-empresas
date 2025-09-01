package cl.bch.segurosempresas.mantenedor.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO de salida estandarizado (alineado a lineamientos BCH).
 * - Se incluye 'codigo' como primer campo de salida.
 * - Se fija el orden de propiedades en el JSON.
 */
@JsonPropertyOrder({
        "codigo",
        "mensaje",
        "estado",
        "estadoCreacion",
        "items",
        "cantidad",
        "totalAprobadas",
        "totalRechazadas",
        "totalPendientes"
})
public class ResumenSolicitudesResponseDto {

    /** Código de resultado de la API (0=OK, 1=ERROR por defecto). */
    private Integer codigo;

    /** Mensaje de la API. */
    private String mensaje;

    /** Estado retornado por el SP. */
    private String estado;

    /** Estado de creación retornado por el SP. */
    private String estadoCreacion;

    /** Items del resumen. */
    private List<ResumenSolicitudDto> items = new ArrayList<>();

    /** Cantidad de items. */
    private Integer cantidad;

    /** Totales - según negocio. */
    private Integer totalAprobadas;
    private Integer totalRechazadas;
    private Integer totalPendientes;

    /** ctor vacío requerido por frameworks. */
    public ResumenSolicitudesResponseDto() { }

    public ResumenSolicitudesResponseDto(
            final String estado,
            final String estadoCreacion,
            final String mensaje,
            final List<ResumenSolicitudDto> items,
            final Integer cantidad,
            final Integer totalAprobadas,
            final Integer totalRechazadas,
            final Integer totalPendientes) {
        this.estado = estado;
        this.estadoCreacion = estadoCreacion;
        this.mensaje = mensaje;
        this.items = items;
        this.cantidad = cantidad;
        this.totalAprobadas = totalAprobadas;
        this.totalRechazadas = totalRechazadas;
        this.totalPendientes = totalPendientes;
    }

    // Getters / Setters
    public Integer getCodigo() { return codigo; }
    public void setCodigo(Integer codigo) { this.codigo = codigo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getEstadoCreacion() { return estadoCreacion; }
    public void setEstadoCreacion(String estadoCreacion) { this.estadoCreacion = estadoCreacion; }

    public List<ResumenSolicitudDto> getItems() { return items; }
    public void setItems(List<ResumenSolicitudDto> items) { this.items = items; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Integer getTotalAprobadas() { return totalAprobadas; }
    public void setTotalAprobadas(Integer totalAprobadas) { this.totalAprobadas = totalAprobadas; }

    public Integer getTotalRechazadas() { return totalRechazadas; }
    public void setTotalRechazadas(Integer totalRechazadas) { this.totalRechazadas = totalRechazadas; }

    public Integer getTotalPendientes() { return totalPendientes; }
    public void setTotalPendientes(Integer totalPendientes) { this.totalPendientes = totalPendientes; }
}
