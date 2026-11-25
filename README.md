# Master Martini Backend

## Inicio local en Windows

Requisitos: JDK compatible con Java 21, Windows PowerShell y MySQL en
`localhost:3306`. Crear las bases `ms_auth`, `ms_recetas`, `ms_cursos` y
`ms_contacto`. El usuario configurado por defecto es `root`.

Desde PowerShell, configurar la contraseña de MySQL para esta sesión e iniciar:

```powershell
$env:DB_PASSWORD = 'TU_CONTRASENA_MYSQL'
.\iniciar-microservicios.bat
```

La contraseña se hereda en las consolas de los cuatro microservicios. También
se puede usar `SPRING_DATASOURCE_PASSWORD`, que tiene prioridad sobre la
configuración del archivo, y `SPRING_DATASOURCE_USERNAME` para cambiar el usuario.
No guardar contraseñas reales en archivos versionados.

El BAT utiliza Maven Wrapper y abre una consola por aplicación. La primera
ejecución requiere Internet para descargar Maven y las dependencias.

| Aplicación | Puerto por defecto |
| --- | --- |
| ms-auth | 8094 |
| ms-recetas | 8090 |
| ms-cursos | 8091 |
| ms-contacto | 8092 |
| api_gateway | 8083 |

Esperar el mensaje `Started` en cada consola para confirmar el arranque.
Para detener las aplicaciones, pulsar `Ctrl+C` en cada consola.

La opción `.\iniciar-microservicios.bat --check` comprueba Java y los archivos
locales sin iniciar servicios. No comprueba MySQL ni la descarga de Maven.

## Swagger / OpenAPI

Los cuatro microservicios generan la especificación desde sus controladores y DTO
con Springdoc 3 (compatible con Spring Boot 4). Las 37 operaciones incluyen
resumen, permisos, respuestas, parámetros y esquemas; las restricciones Jakarta
Validation se reflejan en los modelos.

Con los servicios iniciados, abrir **http://localhost:8083/swagger-ui.html**.
El selector superior permite elegir Autenticación y perfil, Recetas, Cursos o
Contacto. El gateway publica las especificaciones en `/v3/api-docs/auth`,
`/v3/api-docs/recetas`, `/v3/api-docs/cursos` y `/v3/api-docs/contacto`.
Los cuatro servicios deben estar disponibles para consultar sus especificaciones.
`MS_AUTH_URL`, `MS_RECETAS_URL`, `MS_CURSOS_URL` y `MS_CONTACTO_URL` permiten
configurar los destinos del gateway, tanto para la API como para su documentación.

También se puede acceder directamente:

| Servicio | Swagger UI | OpenAPI JSON |
| --- | --- | --- |
| ms-auth (6 operaciones) | http://localhost:8094/swagger-ui.html | http://localhost:8094/v3/api-docs |
| ms-recetas (10 operaciones) | http://localhost:8090/swagger-ui.html | http://localhost:8090/v3/api-docs |
| ms-cursos (16 operaciones) | http://localhost:8091/swagger-ui.html | http://localhost:8091/v3/api-docs |
| ms-contacto (5 operaciones) | http://localhost:8092/swagger-ui.html | http://localhost:8092/v3/api-docs |

En cada microservicio `/v3/api-docs.yaml` ofrece la versión YAML. La documentación
se consulta sin iniciar sesión; las rutas de negocio mantienen sus permisos.

### Probar operaciones con JWT

1. Seleccionar Autenticación y perfil y ejecutar `POST /api/auth/register` o
   `POST /api/auth/login` con **Try it out**.
2. Copiar `accessToken` de la respuesta, pulsar **Authorize** e introducir solo
   el token, sin el prefijo `Bearer`.
3. Ejecutar las operaciones privadas. Las administrativas requieren un token con
   rol `ADMIN`; registro, login, catálogos publicados, imágenes y envío de contacto
   son públicos. La carga de imágenes usa `multipart/form-data`, campo `image`.
4. Si el token expira, iniciar sesión y autorizar otra vez. Al cambiar de servicio,
   comprobar la autorización del esquema seleccionado.

Las URLs relativas del esquema hacen que **Try it out** use el mismo origen desde
el que se consulta Swagger, tanto directamente como a través del gateway.
El listado administrativo de cursos exige autenticación en el microservicio y
rol `ADMIN` al acceder por el gateway, conforme a la configuración existente.

### Verificación automática

Ejecutar en cada carpeta (`ms-auth`, `ms-recetas`, `ms-cursos`, `ms-contacto` y
`api_gateway`):

```powershell
.\mvnw.cmd verify
```

En Linux/macOS: `bash mvnw verify`. Se requiere **JDK 21** y acceso a Maven Central.
Las pruebas de los microservicios usan el perfil `test`, H2 en memoria y tablas
creadas por Hibernate; no requieren contraseña ni modifican las bases MySQL.
Esto verifica el arranque y la documentación, pero no sustituye comprobar las
migraciones Liquibase y los flujos funcionales sobre MySQL.

Las pruebas HTTP contrastan la especificación con todas las rutas de los
controladores, validan Swagger UI, JSON/YAML y que las rutas privadas rechacen
solicitudes anónimas. También ejecutan registro/login, perfil, CRUD de recetas,
cursos/categorías/lecciones, publicación, contacto y subida/descarga de imágenes.
El gateway prueba sus cuatro proxies OpenAPI contra un
servidor HTTP simulado, además del selector y los recursos de Swagger UI.
