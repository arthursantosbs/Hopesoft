@echo off
REM ============================================================
REM HOPESOFT - PARAR SISTEMA
REM ============================================================

setlocal enabledelayedexpansion

cd /d C:\HopeSoft

echo.
echo Parando HopeSoft...
echo.

docker compose down

echo.
echo ✅ HopeSoft foi desligado com segurança
echo.
echo Os dados foram salvos.
echo.
echo Para iniciar novamente, execute INICIAR_HOPESOFT.bat
echo.
pause
