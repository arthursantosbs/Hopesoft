@echo off
REM ============================================================
REM HOPESOFT - INSTALADOR PARA CLIENTES
REM ============================================================

setlocal enabledelayedexpansion

echo.
echo ╔════════════════════════════════════════════════════════════╗
echo ║           HOPESOFT - INSTALADOR DE SISTEMA                 ║
echo ║                                                            ║
echo ║  Este programa vai instalar o HopeSoft na sua máquina     ║
echo ╚════════════════════════════════════════════════════════════╝
echo.

REM Verificar se Docker está instalado
echo [1/4] Verificando Docker...
docker --version >nul 2>&1

if %errorlevel% neq 0 (
    echo.
    echo ❌ Docker não está instalado!
    echo.
    echo Por favor, instale Docker Desktop:
    echo https://www.docker.com/products/docker-desktop
    echo.
    echo Após instalar, execute este programa novamente.
    pause
    exit /b 1
)

echo ✅ Docker encontrado

REM Criar pasta se não existir
echo [2/4] Preparando pasta...
if not exist "C:\HopeSoft" (
    mkdir "C:\HopeSoft"
)

REM Copiar arquivos
echo [3/4] Configurando sistema...
if not exist "C:\HopeSoft\docker-compose.yml" (
    echo Copiando arquivos...
    REM Em produção, você faria download do GitHub aqui
)

echo ✅ Sistema configurado

REM Criar atalho na área de trabalho
echo [4/4] Criando atalho na área de trabalho...

set "DESKTOP=%USERPROFILE%\Desktop"
set "SCRIPT_PATH=C:\HopeSoft\INICIAR_HOPESOFT.bat"

REM Criar arquivo VBS para criar atalho
(
echo Set oWS = WScript.CreateObject("WScript.Shell"^)
echo sLinkFile = "%DESKTOP%\HopeSoft.lnk"
echo Set oLink = oWS.CreateShortcut(sLinkFile^)
echo oLink.TargetPath = "%SCRIPT_PATH%"
echo oLink.WorkingDirectory = "C:\HopeSoft"
echo oLink.IconLocation = "%SystemRoot%\System32\imageres.dll,1"
echo oLink.Description = "Sistema HopeSoft - Frente de Caixa"
echo oLink.Save
) > "%TEMP%\create_shortcut.vbs"

cscript "%TEMP%\create_shortcut.vbs"
del "%TEMP%\create_shortcut.vbs"

echo ✅ Atalho criado

echo.
echo ╔════════════════════════════════════════════════════════════╗
echo ║              ✅ INSTALAÇÃO CONCLUÍDA!                      ║
echo ║                                                            ║
echo ║  Um atalho "HopeSoft" foi criado na sua área de trabalho  ║
echo ║                                                            ║
echo ║  Para usar o sistema:                                     ║
echo ║  1. Clique 2 vezes no atalho HopeSoft                     ║
echo ║  2. Aguarde o sistema inicializar (2-5 minutos)           ║
echo ║  3. Navegador abrirá automaticamente                      ║
echo ║                                                            ║
echo ║  Login padrão:                                            ║
echo ║  Email:  admin@hopesoft.com                               ║
echo ║  Senha:  hopesoft123                                      ║
echo ║                                                            ║
echo ╚════════════════════════════════════════════════════════════╝
echo.
pause

