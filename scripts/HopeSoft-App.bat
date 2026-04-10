@echo off
setlocal

cd /d C:\HopeSoft

if not "%1"=="hidden" (
    powershell -Command "Start-Process '%~f0' -ArgumentList 'hidden' -WindowStyle Hidden"
    exit /b 0
)

docker ps >nul 2>&1
if %errorlevel% neq 0 (
    start "" "C:\Program Files\Docker\Docker\Docker.exe" 2>nul
    timeout /t 45 /nobreak >nul
)

docker compose up -d --build >nul 2>&1
timeout /t 12 /nobreak >nul
start "" "http://localhost:8080/index.html"
