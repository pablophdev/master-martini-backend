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
