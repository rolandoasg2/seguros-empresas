package cl.bch.segurosempresas.mantenedor.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO de ingreso de solicitud.
 * - Acepta claves "amigables" del API y las del SP vía @JsonAlias.
 * - Los nombres que el SP espera (deptoBlock, nombreArchivo, rutaArchivo) se aceptan también,
 *   pero recuerda que el Service ya re-mapea antes de invocar el SP.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IngresoSolicitudRequestDto {

    // ===== Campos escalares tomados por el SP con JSON_VALUE =====
    private Integer idRubro;
    private Integer idProducto;

    @Size(max = 4000)
    private String clausula;

    @Size(max = 4000)
    private String observacion;

    /**
     * El banco define estados de negocio tipo "En edición" / "En revisión".
     * Si quieres más flexibilidad, quita @Pattern.
     */
    @Pattern(regexp = "En edición|En revisi\\u00F3n|En revisión", message = "estado inválido")
    private String estado;

    /** Se guarda en payload_json; el SP lo lee como VARCHAR2. */
    private JsonNode materiaAsegurada;

    // ===== Estructuras =====

    @Valid
    @NotNull(message = "Contratante es obligatorio")
    private PersonaDto contratante;

    @Valid
    private List<PersonaDto> asegurados = new ArrayList<>();

    @Valid
    private List<PersonaDto> beneficiarios = new ArrayList<>();

    @Valid
    @NotNull(message = "Se requiere al menos un documento solicitado")
    @Size(min = 1, message = "Se requiere al menos un documento solicitado")
    private List<DocumentoDto> documentosSolicitados = new ArrayList<>();

    @Valid
    @NotNull(message = "Cuestionario es obligatorio")
    private CuestionarioDto cuestionario;

    // ===== Getters / Setters =====

    public Integer getIdRubro() { return idRubro; }
    public void setIdRubro(Integer idRubro) { this.idRubro = idRubro; }

    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }

    public String getClausula() { return clausula; }
    public void setClausula(String clausula) { this.clausula = clausula; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public JsonNode getMateriaAsegurada() { return materiaAsegurada; }
    public void setMateriaAsegurada(JsonNode materiaAsegurada) { this.materiaAsegurada = materiaAsegurada; }

    public PersonaDto getContratante() { return contratante; }
    public void setContratante(PersonaDto contratante) { this.contratante = contratante; }

    public List<PersonaDto> getAsegurados() { return asegurados; }
    public void setAsegurados(List<PersonaDto> asegurados) { this.asegurados = asegurados; }

    public List<PersonaDto> getBeneficiarios() { return beneficiarios; }
    public void setBeneficiarios(List<PersonaDto> beneficiarios) { this.beneficiarios = beneficiarios; }

    public List<DocumentoDto> getDocumentosSolicitados() { return documentosSolicitados; }
    public void setDocumentosSolicitados(List<DocumentoDto> documentosSolicitados) { this.documentosSolicitados = documentosSolicitados; }

    public CuestionarioDto getCuestionario() { return cuestionario; }
    public void setCuestionario(CuestionarioDto cuestionario) { this.cuestionario = cuestionario; }

    // =====================================================================================
    // Tipos anidados
    // =====================================================================================

    /**
     * Persona / Dirección.
     * Se aceptan ambas claves para el departamento: "departamento" (API) y "deptoBlock" (SP).
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PersonaDto {

        @NotBlank @Size(max = 20)
        private String rut;

        @NotBlank @Size(max = 100)
        private String nombre;

        @JsonAlias("apellidoPaterno")
        @Size(max = 100)
        private String apellidoPaterno;

        @JsonAlias("apellidoMaterno")
        @Size(max = 100)
        private String apellidoMaterno;

        @Size(max = 100) private String region;
        @Size(max = 100) private String ciudad;
        @Size(max = 100) private String comuna;
        @Size(max = 200) private String calle;
        @Size(max = 20)  private String numero;

        /**
         * API envía "departamento"; SP espera "deptoBlock".
         * Con @JsonAlias aceptamos ambos nombres.
         */
        @JsonAlias({"departamento","deptoBlock"})
        @Size(max = 50)
        private String departamento;

        @Size(max = 50) private String casa;

        // Getters / Setters
        public String getRut() { return rut; }
        public void setRut(String rut) { this.rut = rut; }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public String getApellidoPaterno() { return apellidoPaterno; }
        public void setApellidoPaterno(String apellidoPaterno) { this.apellidoPaterno = apellidoPaterno; }

        public String getApellidoMaterno() { return apellidoMaterno; }
        public void setApellidoMaterno(String apellidoMaterno) { this.apellidoMaterno = apellidoMaterno; }

        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }

        public String getCiudad() { return ciudad; }
        public void setCiudad(String ciudad) { this.ciudad = ciudad; }

        public String getComuna() { return comuna; }
        public void setComuna(String comuna) { this.comuna = comuna; }

        public String getCalle() { return calle; }
        public void setCalle(String calle) { this.calle = calle; }

        public String getNumero() { return numero; }
        public void setNumero(String numero) { this.numero = numero; }

        public String getDepartamento() { return departamento; }
        public void setDepartamento(String departamento) { this.departamento = departamento; }

        public String getCasa() { return casa; }
        public void setCasa(String casa) { this.casa = casa; }
    }

    /**
     * Documento solicitado.
     * API puede enviar "nombre"/"ruta"; SP extrae "nombreArchivo"/"rutaArchivo".
     * Con @JsonAlias aceptamos ambos.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DocumentoDto {

        @NotBlank
        @JsonAlias({"nombre","nombreArchivo"})
        @Size(max = 255)
        private String nombre;

        @NotBlank
        @JsonAlias({"ruta","rutaArchivo"})
        @Size(max = 1000)
        private String ruta;

        // Getters / Setters
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public String getRuta() { return ruta; }
        public void setRuta(String ruta) { this.ruta = ruta; }
    }

    /**
     * Cuestionario asociado.
     * API puede enviar "nombre"/"ruta"; SP extrae "nombreArchivo"/"rutaArchivo".
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CuestionarioDto {

        @NotBlank
        @JsonAlias({"nombre","nombreArchivo"})
        @Size(max = 255)
        private String nombre;

        @NotBlank
        @JsonAlias({"ruta","rutaArchivo"})
        @Size(max = 1000)
        private String ruta;

        // Getters / Setters
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public String getRuta() { return ruta; }
        public void setRuta(String ruta) { this.ruta = ruta; }
    }
}
