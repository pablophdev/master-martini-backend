#Requires -Version 5.1
<#
.SYNOPSIS
    Arranca los microservicios del proyecto.

.DESCRIPTION
    Toda la configuracion (base de datos y secreto JWT) esta en los
    application.properties de cada modulo. No se necesitan variables de entorno.

    Modulos disponibles:
        auth      ms-auth      http://localhost:8084
        recetas   ms-recetas   http://localhost:8080
        cursos    ms-cursos    http://localhost:8081
        contacto  ms-contacto  http://localhost:8082
        gateway   api_gateway  http://localhost:8083
        all       todos en ventanas separadas

.EXAMPLE
    .\run-local.ps1 all

.EXAMPLE
    .\run-local.ps1 recetas
#>
param(
    [Parameter(Position = 0)]
    [ValidateSet('auth', 'recetas', 'cursos', 'contacto', 'gateway', 'all')]
    [string]$Service = 'all'
)

$ErrorActionPreference = 'Stop'

$root = $PSScriptRoot

$modules = [ordered]@{
    auth     = 'ms-auth'
    recetas  = 'ms-recetas'
    cursos   = 'ms-cursos'
    contacto = 'ms-contacto'
    gateway  = 'api_gateway'
}

function Start-BackendWindow {
    param([string]$Module)

    $directory = Join-Path $root $Module
    $command = "Set-Location '$directory'; .\mvnw.cmd spring-boot:run"
    Start-Process -FilePath 'powershell.exe' -ArgumentList '-NoExit', '-Command', $command | Out-Null
    Write-Host "  $Module -> nueva ventana" -ForegroundColor DarkGray
}

if ($Service -eq 'all') {
    Write-Host 'Iniciando todos los servicios...' -ForegroundColor Cyan
    foreach ($name in @($modules.Keys)) {
        Start-BackendWindow $modules[$name]
    }
    Write-Host ''
    Write-Host 'Gateway:          http://localhost:8083' -ForegroundColor Green
    Write-Host 'Android emulador: http://10.0.2.2:8083/' -ForegroundColor Green
    return
}

$module = $modules[$Service]
Write-Host "Iniciando $module..." -ForegroundColor Cyan

Push-Location (Join-Path $root $module)
try {
    & .\mvnw.cmd spring-boot:run
}
finally {
    Pop-Location
}
