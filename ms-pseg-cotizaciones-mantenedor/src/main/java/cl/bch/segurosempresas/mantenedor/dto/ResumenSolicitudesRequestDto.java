package cl.bch.segurosempresas.mantenedor.dto;

/**
 * DTO de entrada para consultar el resumen de solicitudes.
 */
public class ResumenSolicitudesRequestDto {

    private Integer idUsuario;  // IN NUMBER
    private String  fecha;      // IN DATE (String: yyyy-MM-dd)
    private String  rol;        // IN VARCHAR2 (EJE, SUP, COOR)

    public ResumenSolicitudesRequestDto() { }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
