@echo off
REM ============================================================
REM HOPESOFT-SETUP.EXE - Criar Executável Nativo
REM ============================================================
REM Este script cria um executável .EXE que funciona como app
REM ============================================================

cd /d "%~dp0.."

echo.
echo Criando HopeSoft.exe (app executável)...
echo.

REM Criar arquivo VB.NET
(
echo Imports System.Diagnostics
echo Imports System.Net
echo Imports System.Threading
echo
echo Module HopeSoftApp
echo     Sub Main()
echo         Try
echo             ' Iniciar Docker se necessário
echo             Dim psi As New ProcessStartInfo()
echo             psi.FileName = "docker"
echo             psi.Arguments = "ps"
echo             psi.UseShellExecute = False
echo             psi.RedirectStandardOutput = True
echo             psi.CreateNoWindow = True
echo
echo             Dim process As Process = Process.Start(psi)
echo             process.WaitForExit()
echo
echo             ' Se Docker não estiver rodando
echo             If process.ExitCode ^<> 0 Then
echo                 ' Abrir Docker Desktop
echo                 Process.Start("C:\Program Files\Docker\Docker\Docker.exe")
echo                 Thread.Sleep(60000)
echo             End If
echo
echo             ' Mudar para pasta HopeSoft
echo             My.Computer.FileSystem.CurrentDirectory = "C:\HopeSoft"
echo
echo             ' Iniciar containers em background
echo             psi.FileName = "docker"
echo             psi.Arguments = "compose up -d"
echo             Process.Start(psi).WaitForExit()
echo
echo             ' Aguardar sistema ficar pronto
echo             Thread.Sleep(10000)
echo
echo             ' Abrir navegador
echo             Process.Start("http://localhost:8080/index.html")
echo
echo         Catch ex As Exception
echo             MsgBox("Erro: " ^& ex.Message)
echo         End Try
echo     End Sub
echo End Module
) > "%TEMP%\HopeSoft.vb"

REM Compilar com VB.NET (se disponível)
if exist "C:\Program Files\Microsoft Visual Studio\2022\Community\MSBuild\Current\Bin\vbc.exe" (
    echo Compilando com Visual Studio...
    "C:\Program Files\Microsoft Visual Studio\2022\Community\MSBuild\Current\Bin\vbc.exe" /target:exe /out:HopeSoft.exe "%TEMP%\HopeSoft.vb"

    if exist "HopeSoft.exe" (
        echo.
        echo ✅ HopeSoft.exe criado com sucesso!
        echo.
        echo Agora você pode usar HopeSoft.exe como um app normal
        echo.
    )
) else (
    echo.
    echo ℹ️  Visual Studio não encontrado
    echo.
    echo Usaremos o método alternativo (HopeSoft-App.bat)
    echo Funciona da mesma forma, apenas visualmente diferente
    echo.
)

del "%TEMP%\HopeSoft.vb" 2>nul

pause
