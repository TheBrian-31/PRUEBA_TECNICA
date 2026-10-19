# Coworking

API REST para reservar espacios de coworking. La aplicación y PostgreSQL se levantan con Docker Compose desde la carpeta `coworking`.

## Requisitos Técnicos

- Docker.
- Para correr las pruebas en tu máquina: Java 17. 

## Levantar todos los contenedores

Posicionate en la carpeta `coworking` y ejecuta el comando:

```powershell
cd coworking
docker compose up -d
```

Con eso se crea la base de datos, se corren los scripts de `src/main/resources/db`, se construye y arranca la aplicación.

Cuando el contenedor `coworking-app` ya esté arriba, la API queda en `http://localhost:8080`. La documentación de la API está en `http://localhost:8080/swagger-ui.html`.

## Actualizar contenedor de la API

Si solo se cambia el código de la aplicación, reconstruye y reiniciá ese servicio (PostgreSQL no se vuelve a crear).

```powershell
cd coworking
docker compose up -d --build --no-deps app
```

Si lo que querés es volver a levantar todos los servicios que ya estaban creados, usá:

```powershell
docker compose up -d
```

## Pruebas

Corré los comandos desde la carpeta `coworking`. La prueba de integración levanta PostgreSQL con Testcontainers, por lo cual Docker tiene que estar corriendo.

Solo la prueba unitaria de ocupación:

```powershell
.\mvnw.cmd "-Dtest=OccupancyServiceTest" test
```

Solo la prueba de integración del flujo de reservas:

```powershell
.\mvnw.cmd "-Dtest=ReservationFlowIntegrationTest" test
```

Todas las pruebas:

```powershell
.\mvnw.cmd test
```
