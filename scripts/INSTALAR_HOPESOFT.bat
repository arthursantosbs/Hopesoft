@echo off
setlocal

set "SOURCE_DIR=%~dp0.."
set "TARGET_DIR=C:\HopeSoft"
set "DESKTOP=%USERPROFILE%\Desktop"
set "SHORTCUT=%DESKTOP%\HopeSoft.lnk"

echo.
echo Preparando instalacao local do HopeSoft...
echo.

docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo Docker Desktop nao foi encontrado.
    echo Instale o Docker e execute este instalador novamente.
    pause
    exit /b 1
)

if not exist "%TARGET_DIR%" mkdir "%TARGET_DIR%"

robocopy "%SOURCE_DIR%" "%TARGET_DIR%" /E /XD .git .idea backend\target target /XF app_output.log >nul
if %errorlevel% geq 8 (
    echo Falha ao copiar os arquivos para %TARGET_DIR%.
    pause
    exit /b 1
)

(
echo Set shell = CreateObject("WScript.Shell"^)
echo Set shortcut = shell.CreateShortcut("%SHORTCUT%"^)
echo shortcut.TargetPath = "%TARGET_DIR%\scripts\HopeSoft-App.bat"
echo shortcut.WorkingDirectory = "%TARGET_DIR%"
echo shortcut.IconLocation = "%SystemRoot%\System32\imageres.dll,1"
echo shortcut.Description = "HopeSoft"
echo shortcut.Save
) > "%TEMP%\hopesoft_shortcut.vbs"

cscript //nologo "%TEMP%\hopesoft_shortcut.vbs"
del "%TEMP%\hopesoft_shortcut.vbs"

echo.
echo Instalacao concluida.
echo Atalho criado em: %SHORTCUT%
echo Login inicial: admin@hopesoft.com / hopesoft123
echo.
pause
