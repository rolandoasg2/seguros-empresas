package cl.bch.segurosempresas.mantenedor;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import cl.bch.segurosempresas.mantenedor.MsPsegCotizacionesMantenedorApplication;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Pruebas para la inicialización del contexto de la aplicación.
 * Cumple con la Sección 2.2 del arquetipo: Compilación y ejecución.
 */
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = MsPsegCotizacionesMantenedorApplication.class)
class MsPsegCotizacionesMantenedorApplicationTests {

    /**
     * Constructor por defecto para la clase de pruebas.
     */
    public MsPsegCotizacionesMantenedorApplicationTests() {
    }

    /**
     * Verifica que el contexto de la aplicación se cargue correctamente.
     */
    @Test
    void contextLoads() {
        assertDoesNotThrow(() -> {
            // Prueba básica para verificar que el contexto se carga sin errores
        });
    }
}