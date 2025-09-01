package cl.bch.segurosempresas.mantenedor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * Entry point del microservicio Seguros Empresas.
 * NOTA: No usar 'final' en clases @Configuration/@SpringBootApplication.
 */
@SpringBootApplication
@SuppressWarnings("PMD.UseUtilityClass") // Esta clase no es "utility", es el entry point.
//@PropertySource({"file:${APPS_PROPS}/Config/application.yml"})
public class MsPsegCotizacionesMantenedorApplication {

    /** Constructor por defecto requerido para evitar falsos positivos de PMD. */
    public MsPsegCotizacionesMantenedorApplication() {
        // Intencionalmente vacío
    }

    public static void main(final String[] args) {
        SpringApplication.run(MsPsegCotizacionesMantenedorApplication.class, args);
    }
}
