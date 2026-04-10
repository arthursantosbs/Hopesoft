@echo off
REM ============================================================
REM HOPESOFT - APP DESKTOP (sem terminal visível)
REM ============================================================

setlocal enabledelayedexpansion

REM Definir pasta
cd /d C:\HopeSoft

REM Esconder janela
if not "%1"=="hidden" (
    powershell -Command "Start-Process '%~f0' -ArgumentList 'hidden' -WindowStyle Hidden"
    exit /b 0
)

REM Verificar se Docker está rodando
docker ps >nul 2>&1

if %errorlevel% neq 0 (
    REM Tentar abrir Docker Desktop
    start "" "C:\Program Files\Docker\Docker\Docker.exe" 2>nul

    REM Aguardar inicialização
    timeout /t 60 /nobreak
)

REM Verificar se sistema já está rodando
docker ps | findstr hopesoft-app >nul 2>&1

if %errorlevel% neq 0 (
    REM Iniciar containers
    docker compose up -d
)

REM Aguardar sistema ficar pronto
timeout /t 10

REM Abrir navegador (sem esperar)
start "" "http://localhost:8080/index.html"

REM Manter script rodando
:loop
docker ps | findstr hopesoft-app >nul 2>&1
if %errorlevel% equ 0 (
    timeout /t 30
    goto loop
)

REM Se container parou, encerrar
exit /b 0
