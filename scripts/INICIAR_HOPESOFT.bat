@echo off
setlocal

cd /d C:\HopeSoft

echo.
echo Iniciando HopeSoft...
echo.

docker ps >nul 2>&1
if %errorlevel% neq 0 (
    echo Docker nao esta rodando. Tentando abrir o Docker Desktop...
    start "" "C:\Program Files\Docker\Docker\Docker.exe" 2>nul
    timeout /t 45 /nobreak >nul
)

docker compose up -d --build
if %errorlevel% neq 0 (
    echo.
    echo Falha ao iniciar os containers.
    pause
    exit /b 1
)

echo.
echo Aguardando a aplicacao responder...
timeout /t 12 /nobreak >nul

start "" "http://localhost:3000"

echo.
echo HopeSoft iniciado.
echo Frontend: http://localhost:3000
echo Backend:  http://localhost:8080
echo.
pause
