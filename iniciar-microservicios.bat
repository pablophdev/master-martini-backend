@echo off
setlocal EnableExtensions DisableDelayedExpansion
rem Requisitos: JDK 21 compatible, MySQL y acceso a Internet en el primer arranque.
rem Bases MySQL: ms_auth, ms_recetas, ms_cursos y ms_contacto.
rem Las credenciales se configuran en src/main/resources/application.properties.
rem Uso: iniciar-microservicios.bat [--check]
rem --check comprueba Java y archivos locales sin iniciar aplicaciones.

set "ROOT=%~dp0"
if not "%~1"=="" if /i not "%~1"=="--check" (
    echo Uso: %~nx0 [--check]
    exit /b 1
)

if defined JAVA_HOME (
    if not exist "%JAVA_HOME%\bin\javac.exe" (
        echo [ERROR] JAVA_HOME debe apuntar a un JDK, no a un JRE.
        goto :error
    )
    set "PATH=%JAVA_HOME%\bin;%PATH%"
)
where java.exe >nul 2>&1
if errorlevel 1 (
    echo [ERROR] No se encuentra Java. Instala JDK 21 y configura JAVA_HOME o PATH.
    goto :error
)
where javac.exe >nul 2>&1
if errorlevel 1 (
    echo [ERROR] No se encuentra javac. Instala JDK 21 y configura JAVA_HOME o PATH.
    goto :error
)
powershell.exe -NoProfile -Command "$v = & javac.exe -version 2>&1; if ($LASTEXITCODE -ne 0 -or [string]$v -notmatch 'javac (\d+)' -or [int]$Matches[1] -lt 21) { exit 1 }; Write-Host $v"
if errorlevel 1 (
    echo [ERROR] Se necesita un JDK compatible con Java 21 y Windows PowerShell.
    goto :error
)

for %%S in (ms-auth ms-recetas ms-cursos ms-contacto api_gateway) do (
    for %%F in (pom.xml mvnw.cmd .mvn\wrapper\maven-wrapper.properties) do (
        if not exist "%ROOT%%%S\%%F" (
            echo [ERROR] Falta el archivo "%ROOT%%%S\%%F".
            goto :error
        )
    )
)

echo.
echo Configuracion local por defecto:
echo   ms-auth       http://localhost:8094
echo   ms-recetas    http://localhost:8090
echo   ms-cursos     http://localhost:8091
echo   ms-contacto   http://localhost:8092
echo   api_gateway   http://localhost:8083
echo.
echo MySQL debe estar disponible y las cuatro bases de datos deben existir.
echo Se utilizan las configuraciones y variables de entorno de Spring Boot.
if /i "%~1"=="--check" (
    echo [OK] Java y archivos locales comprobados. No se verifico MySQL ni Maven.
    exit /b 0
)

for %%S in (ms-auth ms-recetas ms-cursos ms-contacto api_gateway) do (
    echo Lanzando %%S...
    start "Master Martini - %%S" /D "%ROOT%%%S" "%ComSpec%" /D /K "call mvnw.cmd spring-boot:run"
    if errorlevel 1 (
        echo [ERROR] No se pudo abrir la consola de %%S.
        goto :error
    )
)
echo.
echo Consolas abiertas. Espera el mensaje Started en cada una para confirmar el arranque.
echo La primera ejecucion puede tardar mientras Maven descarga las dependencias.
echo Para detener los servicios, pulsa Ctrl+C en cada consola.
exit /b 0

:error
if /i not "%~1"=="--check" pause
exit /b 1
