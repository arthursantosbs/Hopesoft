HOPESOFT - Scripts Windows (Batch)
==================================

Implementacao dos .bat do projeto. Na raiz do repositorio existem arquivos com o MESMO NOME que apenas redirecionam para esta pasta (para quem clica duas vezes na raiz).

Arquivos:
- INSTALAR_HOPESOFT.bat   Instalador (atalho no Desktop -> C:\HopeSoft)
- INICIAR_HOPESOFT.bat    Sobe Docker em C:\HopeSoft e abre o app
- PARAR_HOPESOFT.bat      docker compose down em C:\HopeSoft
- HopeSoft-App.bat        Inicia sem terminal visivel (fluxo cliente)
- CRIAR_EXE.bat           Gera HopeSoft.exe na RAIZ DO REPOSITORIO (requer VS)
- COMPILAR_EXE.bat        Alternativa C# para .exe na RAIZ DO REPOSITORIO

Desenvolvimento local: use Docker na pasta do clone (docker-compose.yml na raiz), nao apenas C:\HopeSoft, salvo se voce copiar o projeto para la.
