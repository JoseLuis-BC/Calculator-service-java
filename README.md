# Calculator Service

API REST de calculadora con autenticacion JWT, historial por usuario y validacion externa de correo.

## a) Instrucciones de instalacion

### Requisitos

- Java 21 o superior (Spring Boot 3.2.5)
- Gradle instalado globalmente (el proyecto no incluye `gradlew`)
- MySQL accesible desde la aplicacion (la BD esta en la nube)
- Conexion a internet para validacion de correos (Abstract API)

### Instalacion y ejecucion

1. Clonar el repositorio y entrar a la carpeta del proyecto.
2. Compilar:

```bash
gradle clean build
```

3. Ejecutar:

```bash
gradle bootRun
```

4. Verificar servicio:

- Base URL: `http://localhost:8081/raven`
- Swagger UI: `http://localhost:8081/raven/swagger-ui/index.html`

## b) Configuracion de base de datos y API externa

La configuracion activa por defecto usa el perfil `prod`:

```yaml
spring:
  profiles:
    active:
      - prod
```

### Base de datos (MySQL)

En `src/main/resources/application-prod.yml` se usa:

```yaml
spring:
  datasource:
    url: jdbc:mysql://<host>:<port>/<database>
```

Ademas:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
```

### JWT

```yaml
raven:
  secret: <jwt-secret>
security:
  jwt:
    expiration: 300000
```

### API externa de validacion de correo

Registro de usuarios llama a Abstract Email Validation API.

```yaml
external:
  email:
    api:
      key: <abstract-api-key>
```

Recomendacion para entornos reales: no guardar secretos en YAML versionado; usar variables de entorno o gestor de secretos.

## c) Ejemplos de uso con curl/httpie

URL base:

```bash
BASE_URL="http://localhost:8081/raven"
```

### 1. Registro

curl:

```bash
curl -X POST "$BASE_URL/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "demo_user",
    "password": "demo_pass_123",
    "email": "demo_user@example.com"
  }'
```

httpie:

```bash
http POST "$BASE_URL/api/auth/register" \
  username=demo_user password=demo_pass_123 email=demo_user@example.com
```

### 2. Login

curl:

```bash
curl -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "demo_user",
    "password": "demo_pass_123"
  }'
```

httpie:

```bash
http POST "$BASE_URL/api/auth/login" \
  username=demo_user password=demo_pass_123
```

Respuesta esperada (ejemplo):

```json
{
  "token_raven": "<jwt>",
  "userId": "<uuid>",
  "username": "demo_user",
  "email": "demo_user@example.com"
}
```

### 3. Calcular (requiere JWT)

Operaciones soportadas:
`ADDITION`, `SUBTRACTION`, `MULTIPLICATION`, `DIVISION`, `SQRT`, `POWER`, `MODULO`, `ABSOLUTE`, `MAX`, `MIN`.

curl:

```bash
TOKEN="<jwt>"

curl -X POST "$BASE_URL/api/calculate" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "operation": "ADDITION",
    "operandA": 10,
    "operandB": 25
  }'
```

httpie:

```bash
http POST "$BASE_URL/api/calculate" \
  Authorization:"Bearer <jwt>" \
  operation=ADDITION operandA:=10 operandB:=25
```

Nota:
- Para `SQRT` y `ABSOLUTE`, `operandB` no es requerido.
- El rango permitido para operandos es de `-1000000` a `1000000`.

### 4. Consultar historial

curl:

```bash
curl -X GET "$BASE_URL/api/history?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"
```

Con filtro por tipo y fechas (ISO-8601):

```bash
curl -X GET "$BASE_URL/api/history?operation=DIVISION&startDate=2026-01-01T00:00:00&endDate=2026-12-31T23:59:59&page=0&size=20" \
  -H "Authorization: Bearer $TOKEN"
```

httpie:

```bash
http GET "$BASE_URL/api/history" \
  Authorization:"Bearer <jwt>" \
  page==0 size==10
```

### 5. Obtener y eliminar operacion por ID

curl (obtener):

```bash
curl -X GET "$BASE_URL/api/history/<operation-id>" \
  -H "Authorization: Bearer $TOKEN"
```

curl (eliminar):

```bash
curl -X DELETE "$BASE_URL/api/history/<operation-id>" \
  -H "Authorization: Bearer $TOKEN"
```

httpie (obtener):

```bash
http GET "$BASE_URL/api/history/<operation-id>" Authorization:"Bearer <jwt>"
```

httpie (eliminar):

```bash
http DELETE "$BASE_URL/api/history/<operation-id>" Authorization:"Bearer <jwt>"
```

## d) Decisiones tecnicas tomadas

1. Spring Boot 3 + Spring Security con JWT stateless.
   - Se evita sesion en servidor y se protege casi todo el API salvo autenticacion y Swagger.

2. Arquitectura por capas (controller, service, repository, strategy).
   - Facilita separacion de responsabilidades y pruebas aisladas.

3. Patron Strategy para operaciones matematicas.
   - Cada operacion tiene implementacion propia y se resuelve por factory.
   - Permite agregar operaciones nuevas sin romper logica existente.

4. Persistencia con Spring Data JPA y entidad de historial.
   - Cada calculo queda asociado al usuario y puede consultarse o eliminarse por ID.

5. Guardado asincrono de operaciones.
   - La respuesta del calculo no queda bloqueada por la escritura en BD para optimizar la respuesta.

6. Validacion de email en registro con API externa.
   - Reduce cuentas con correos no validos o desechables desde el alta, se utilizo Abstractapi por su facilidad de implementacion y con la version de prueba da 100 tokens.

7. Manejo centralizado de errores y logging estructurado.
   - Mejora trazabilidad operativa y consistencia en respuestas de error.
