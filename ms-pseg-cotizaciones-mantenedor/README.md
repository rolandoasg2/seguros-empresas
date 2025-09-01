# Seguros Empresas Microservice

## Descripción
Microservicio para consultar el resumen de solicitudes de seguros empresariales, integrado con Oracle OCI mediante JNDI.

## Tecnologías
- Java 21
- Spring Boot 3.3.4
- Spring Cloud 2023.0.3
- Maven 3.8.x
- Oracle Database (JNDI: java:comp/env/jdbc/ocimarcelo)

## Configuración
- Perfil `dev`: Usa JNDI para conectar a Oracle OCI en desarrollo.
- Perfil `prod`: Usa JNDI para conectar a Oracle OCI en producción.
- Perfil `test`: Usa H2 para pruebas unitarias.

## Ejecución
```bash
mvn clean package
copy target\seguros-empresas.war D:\u\apache-tomcat-10.1.44_j21\webapps