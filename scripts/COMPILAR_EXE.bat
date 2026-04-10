@echo off
REM ============================================================
REM HOPESOFT - COMPILAR EXE NATIVO
REM ============================================================

setlocal enabledelayedexpansion

cd /d "%~dp0.."

echo.
echo ╔════════════════════════════════════════════════════════════╗
echo ║        HOPESOFT - COMPILADOR PARA EXE NATIVO              ║
echo ╚════════════════════════════════════════════════════════════╝
echo.

REM Verificar se C# está disponível
echo [1/3] Verificando ferramentas...

set CSC_PATH=
for /f "delims=" %%A in ('where csc.exe 2^>nul') do set "CSC_PATH=%%A"

if "!CSC_PATH!"=="" (
    echo ❌ C# Compiler não encontrado
    echo.
    echo Procurando alternativas...

    REM Tentar encontrar o Framework
    if exist "C:\Windows\Microsoft.NET\Framework64\v4.0.30319\csc.exe" (
        set "CSC_PATH=C:\Windows\Microsoft.NET\Framework64\v4.0.30319\csc.exe"
        echo ✅ Encontrado: !CSC_PATH!
    ) else (
        echo.
        echo ❌ .NET Framework não encontrado
        echo.
        echo Para criar EXE nativo, instale:
        echo https://dotnet.microsoft.com/download
        echo.
        pause
        exit /b 1
    )
)

echo ✅ Ferramentas encontradas

REM Criar código C# (Windows Forms com WebView2)
echo [2/3] Gerando código...

(
echo using System;
echo using System.Diagnostics;
echo using System.Windows.Forms;
echo using System.Threading.Tasks;
echo.
echo namespace HopeSoft
echo {
echo     static class Program
echo     {
echo         [STAThread]
echo         static void Main()
echo         {
echo             Application.EnableVisualStyles();
echo             Application.SetCompatibleTextRenderingDefault(false);
echo
echo             // Iniciar Docker
echo             Task.Run(() =^> InitializeDocker());
echo
echo             // Abrir formulário
echo             Application.Run(new MainForm());
echo         }
echo.
echo         static void InitializeDocker()
echo         {
echo             try
echo             {
echo                 // Verificar Docker
echo                 ProcessStartInfo psi = new ProcessStartInfo("docker", "ps");
echo                 psi.RedirectStandardOutput = true;
echo                 psi.UseShellExecute = false;
echo                 psi.CreateNoWindow = true;
echo
echo                 Process process = Process.Start(psi);
echo                 process.WaitForExit();
echo
echo                 // Se Docker não estiver rodando
echo                 if (process.ExitCode != 0)
echo                 {
echo                     // Abrir Docker Desktop
echo                     Process.Start("C:\\Program Files\\Docker\\Docker\\Docker.exe");
echo                     System.Threading.Thread.Sleep(60000);
echo                 }
echo
echo                 // Mudar para pasta HopeSoft
echo                 System.IO.Directory.SetCurrentDirectory("C:\\HopeSoft");
echo
echo                 // Iniciar containers
echo                 psi = new ProcessStartInfo("docker", "compose up -d");
echo                 psi.UseShellExecute = false;
echo                 psi.CreateNoWindow = true;
echo                 Process.Start(psi).WaitForExit();
echo             }
echo             catch { }
echo         }
echo     }
echo.
echo     public class MainForm : Form
echo     {
echo         private WebBrowser browser;
echo.
echo         public MainForm()
echo         {
echo             this.Text = "HopeSoft - Sistema de Vendas";
echo             this.Size = new System.Drawing.Size(1200, 800);
echo             this.StartPosition = FormStartPosition.CenterScreen;
echo             this.Icon = SystemIcons.Application;
echo.
echo             browser = new WebBrowser();
echo             browser.Dock = DockStyle.Fill;
echo             browser.ScriptErrorsSuppressed = true;
echo             this.Controls.Add(browser);
echo.
echo             this.Load += (s, e) =^> NavigateToApp();
echo         }
echo.
echo         private void NavigateToApp()
echo         {
echo             System.Threading.Thread.Sleep(5000);
echo             browser.Navigate("http://localhost:8080/index.html");
echo         }
echo     }
echo }
) > "%TEMP%\HopeSoft.cs"

echo ✅ Código gerado

REM Compilar
echo [3/3] Compilando...

"!CSC_PATH!" /target:winexe /out:HopeSoft.exe "%TEMP%\HopeSoft.cs"

if exist "HopeSoft.exe" (
    echo.
    echo ╔════════════════════════════════════════════════════════════╗
    echo ║                ✅ SUCESSO!                                ║
    echo ║                                                            ║
    echo ║  HopeSoft.exe foi criado com sucesso!                     ║
    echo ║                                                            ║
    echo ║  Você pode agora:                                         ║
    echo ║  1. Usar HopeSoft.exe como um programa normal             ║
    echo ║  2. Criar atalho no Desktop                               ║
    echo ║  3. Fixar na Barra de Tarefas                             ║
    echo ║  4. Distribuir para clientes                              ║
    echo ║                                                            ║
    echo ║  O app roda completamente sem terminal!                   ║
    echo ║                                                            ║
    echo ╚════════════════════════════════════════════════════════════╝
    echo.
) else (
    echo.
    echo ❌ Erro ao compilar
    echo.
    echo Verifique o caminho do compilador C#
    echo.
)

del "%TEMP%\HopeSoft.cs" 2>nul

pause

