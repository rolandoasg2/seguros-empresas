# Docker Setup

## Build local WAR y correr contenedor
```bash
mvn clean package -DskipTests
docker build -t seguros-empresas:1.0.0 .
docker run --rm -p 8080:8080 hello-world:1.0.0
```

## Multi-stage build (todo dentro de Docker)
```bash
docker build -t seguros-empresas:1.0.0 .
docker run --rm -p 8080:8080 seguros-empresas:1.0.0
```

## Docker Compose
```bash
docker compose up --build
```
Esto levantará la API en el puerto 8080.

## Perfiles
El contenedor soporta el env var `SPRING_PROFILES_ACTIVE`.  
Por defecto está en `prod`, para desarrollo usa:
```bash
docker run -e SPRING_PROFILES_ACTIVE=dev -p 8080:8080 seguros-empresas:1.0.0
```
