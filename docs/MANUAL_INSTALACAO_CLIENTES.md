# Manual de Instalacao para Clientes

## Objetivo

Este guia serve para instalar o HopeSoft em um computador Windows usando Docker Desktop.

## Pre-requisitos

- Windows com permissao para instalar programas
- Docker Desktop instalado e aberto
- porta `8080` livre
- pelo menos 4 GB de RAM disponiveis

## Instalacao

### Opcao recomendada

1. Abra a pasta do projeto.
2. Execute `scripts\INSTALAR_HOPESOFT.bat`.
3. Aguarde a copia para `C:\HopeSoft`.
4. Use o atalho `HopeSoft` criado na area de trabalho.

### Acesso inicial

- Email: `admin@hopesoft.com`
- Senha: `hopesoft123`

## Operacao diaria

### Iniciar

Use o atalho da area de trabalho ou execute:

```bat
C:\HopeSoft\scripts\INICIAR_HOPESOFT.bat
```

### Parar

```bat
C:\HopeSoft\scripts\PARAR_HOPESOFT.bat
```

## Atualizacao

1. Atualize os arquivos do projeto fonte.
2. Execute novamente `scripts\INSTALAR_HOPESOFT.bat`.
3. Inicie o sistema normalmente.

## Problemas comuns

### Docker nao abre

- abra o Docker Desktop manualmente
- espere alguns segundos e tente iniciar novamente

### Porta 8080 ocupada

- feche outro sistema que esteja usando essa porta
- ou ajuste a porta no `docker-compose.yml`

### Sistema nao responde

Use na pasta `C:\HopeSoft`:

```powershell
docker compose logs -f
```

## Observacao importante

Esta instalacao e ideal para piloto local e validacao operacional. Para producao estavel, o recomendado e configurar rotina de backup, atualizacao controlada e monitoramento.
