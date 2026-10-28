# Backend de Envío de Correos (Java Spring Boot)

## Arquitectura

```
Angular (Frontend) -> Java Spring Boot (Backend) -> Cloudflare Worker -> SendGrid
```

El backend Java **no envía correos directamente a SendGrid**. Únicamente recibe la solicitud, valida los datos y reenvía los campos `destinatario`, `asunto` y `mensaje` al Cloudflare Worker mediante HTTP POST, incluyendo el encabezado `X-Worker-Token`. El Worker se encarga de la comunicación con SendGrid.

## Requisitos

- Java 17
- Maven (o Maven Wrapper incluido)

## Endpoints

| Método | Ruta | Descripción |
| --- | --- | --- |
| POST | `/api/correos/enviar` | Envía un correo. Body: `{"destinatario": "...", "asunto": "...", "mensaje": "..."}` |
| GET | `/api/health` | Health check. Respuesta: `{"estado": "OK"}` |

## Variables de entorno

| Variable | Descripción | Valor por defecto |
| --- | --- | --- |
| `PORT` | Puerto en el que escucha el servidor (requerido por Render) | `8080` |
| `CLOUDFLARE_WORKER_URL` | URL completa del Cloudflare Worker (ej. `https://mi-worker.workers.dev/enviar`) | - |
| `WORKER_AUTH_TOKEN` | Token de autenticación para el Worker. Se envía como header `X-Worker-Token` | - |
| `FRONTEND_ORIGIN` | Orígenes permitidos para CORS (separados por coma si son varios) | `http://localhost:4200` |

## Configuración local (PowerShell)

```powershell
$env:PORT="8080"
$env:CLOUDFLARE_WORKER_URL="https://tu-worker.workers.dev/enviar"
$env:WORKER_AUTH_TOKEN="tu-token-secreto"
$env:FRONTEND_ORIGIN="http://localhost:4200"
```

## Ejecución local

### Con Maven Wrapper (recomendado)

```powershell
.\mvnw.cmd spring-boot:run
```

### Con Maven instalado

```powershell
mvn spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`.

## Compilación y pruebas

### Compilar

```powershell
.\mvnw.cmd clean compile
```

### Ejecutar tests

```powershell
.\mvnw.cmd test
```

### Empaquetar

```powershell
.\mvnw.cmd clean package
```

El JAR generado se encontrará en `target/email-0.0.1-SNAPSHOT.jar`.

## Despliegue en Render

1. **Subir el repositorio** a GitHub/GitLab.
2. **Crear un nuevo Web Service** en [Render](https://render.com/).
3. **Conectar el repositorio**.
4. **Configurar Build Command**:
   ```bash
   ./mvnw clean package -DskipTests
   ```
5. **Configurar Start Command**:
   ```bash
   java -jar target/email-0.0.1-SNAPSHOT.jar
   ```
6. **Agregar variables de entorno** en Render (Environment):
   - `CLOUDFLARE_WORKER_URL` = URL de tu Cloudflare Worker
   - `WORKER_AUTH_TOKEN` = Token de autenticación del Worker
   - `FRONTEND_ORIGIN` = URL de tu frontend en producción (ej. `https://tu-app-angular.onrender.com`)
   - `PORT` = Render lo inyecta automáticamente, pero es recomendable tenerlo configurado (la app lo lee)
7. **Desplegar**. Render asignará el puerto mediante la variable `PORT` y la app lo utilizará correctamente.

## Notas de seguridad

- El backend **nunca** devuelve tokens ni detalles internos en las respuestas de error.
- Las validaciones se realizan con Jakarta Validation. Errores de validación devuelven HTTP 400.
- Errores mapeados: 401 (No autorizado), 429 (Demasiadas solicitudes), 502 (Error de conexión con el servicio de correo).
- CORS está configurado para desarrollo local y parametrizado para producción.
