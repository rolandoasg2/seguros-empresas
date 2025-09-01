package cl.bch.segurosempresas.mantenedor.dto;

/**
 * Respuesta estándar de ingreso de solicitud.
 */
public class IngresoSolicitudResponseDto {

    private int codigo;              // 0 = OK; !=0 error negocio
    private String mensaje;          // mensaje humano
    private String estado;           // OK / ERROR
    private String estadoCreacion;   // detalle del SP
    private Long idSolicitud;        // opcional

    public int getCodigo() { return codigo; }
    public void setCodigo(int v) { this.codigo = v; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String v) { this.mensaje = v; }
    public String getEstado() { return estado; }
    public void setEstado(String v) { this.estado = v; }
    public String getEstadoCreacion() { return estadoCreacion; }
    public void setEstadoCreacion(String v) { this.estadoCreacion = v; }
    public Long getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(Long v) { this.idSolicitud = v; }
}
