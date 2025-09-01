package cl.bch.segurosempresas.mantenedor.dto;

/**
 * DTO para una solicitud individual en el resumen.
 * Campos alineados con las columnas del SELECT del SP.
 */
public class ResumenSolicitudDto {

    private String idSolicitud;
    private String fechaCreacion;
    private String nombreContratante;

    private String rubro;        // NUEVO
    private String tipoSeguro;   // NUEVO
    private String coordinador;  // NUEVO
    private String ejecutivo;    // NUEVO

    private String estadoSolicitud;

    public ResumenSolicitudDto() { }

    public ResumenSolicitudDto(String idSolicitud, String fechaCreacion, String nombreContratante,
                               String rubro, String tipoSeguro, String coordinador, String ejecutivo,
                               String estadoSolicitud) {
        this.idSolicitud = idSolicitud;
        this.fechaCreacion = fechaCreacion;
        this.nombreContratante = nombreContratante;
        this.rubro = rubro;
        this.tipoSeguro = tipoSeguro;
        this.coordinador = coordinador;
        this.ejecutivo = ejecutivo;
        this.estadoSolicitud = estadoSolicitud;
    }

    public String getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(String idSolicitud) { this.idSolicitud = idSolicitud; }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getNombreContratante() { return nombreContratante; }
    public void setNombreContratante(String nombreContratante) { this.nombreContratante = nombreContratante; }

    public String getRubro() { return rubro; }
    public void setRubro(String rubro) { this.rubro = rubro; }

    public String getTipoSeguro() { return tipoSeguro; }
    public void setTipoSeguro(String tipoSeguro) { this.tipoSeguro = tipoSeguro; }

    public String getCoordinador() { return coordinador; }
    public void setCoordinador(String coordinador) { this.coordinador = coordinador; }

    public String getEjecutivo() { return ejecutivo; }
    public void setEjecutivo(String ejecutivo) { this.ejecutivo = ejecutivo; }

    public String getEstadoSolicitud() { return estadoSolicitud; }
    public void setEstadoSolicitud(String estadoSolicitud) { this.estadoSolicitud = estadoSolicitud; }
}
