@echo off
setlocal

cd /d C:\HopeSoft

echo.
echo Encerrando HopeSoft...
echo.

docker compose down

echo.
echo Containers parados com seguranca.
echo.
pause
