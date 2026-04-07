@echo off
REM ============================================================
REM HOPESOFT - INICIAR SISTEMA (Roda como App)
REM ============================================================

setlocal enabledelayedexpansion

REM Definir pasta
cd /d C:\HopeSoft

REM Verificar se Docker está rodando
echo Iniciando HopeSoft...
docker ps >nul 2>&1

if %errorlevel% neq 0 (
    echo.
    echo ❌ Docker não está rodando!
    echo.
    echo Abrindo Docker Desktop...
    REM Tentar abrir Docker Desktop
    start "" "C:\Program Files\Docker\Docker\Docker.exe" 2>nul

    echo.
    echo Aguardando Docker inicializar... (1-2 minutos)
    echo Por favor, não feche esta janela.
    echo.

    REM Aguardar Docker inicializar
    timeout /t 60 /nobreak
)

REM Verificar se containers já estão rodando
docker ps | findstr hopesoft-app >nul 2>&1

if %errorlevel% equ 0 (
    echo.
    echo ✅ HopeSoft já está rodando!
    echo.
    echo Abrindo navegador...
    timeout /t 2

    REM Abrir navegador
    start "" "http://localhost:8080/index.html"

    echo.
    echo Sistema rodando em: http://localhost:8080
    echo.
    echo Para desligar: Feche esta janela e execute PARAR_HOPESOFT.bat
    echo.

    pause
    exit /b 0
)

REM Se não estão rodando, iniciar
echo.
echo Iniciando containers... (primeira vez leva 2-5 minutos)
echo Por favor, aguarde...
echo.

docker compose up --build


