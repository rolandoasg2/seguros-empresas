package cl.bch.segurosempresas.mantenedor;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Inicializador para despliegue como WAR en Tomcat/JBoss/etc.
 */
public class MsPsegCotizacionesMantenedorServletInitializer extends SpringBootServletInitializer {

    /** Constructor explícito para calmar la regla PMD AtLeastOneConstructor. */
    public MsPsegCotizacionesMantenedorServletInitializer() {
        // Intencionalmente vacío
    }

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
        return application.sources(MsPsegCotizacionesMantenedorApplication.class);
    }
}
