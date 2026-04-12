# Manual de Instalacao HopeSoft

## Objetivo

Este guia prepara o HopeSoft para uso local em Windows com Docker Desktop, deixando o sistema pronto para o piloto operacional.

## Pre-requisitos

- Windows com permissao para instalar programas
- Docker Desktop instalado e aberto
- portas `3000`, `5432` e `8080` livres
- pelo menos 4 GB de RAM disponiveis

## Instalacao recomendada

1. Abra a pasta do projeto.
2. Execute `scripts\INSTALAR_HOPESOFT.bat`.
3. Aguarde a copia para `C:\HopeSoft`.
4. Use o atalho `HopeSoft` criado na area de trabalho.

## Enderecos do sistema

- Frontend: `http://localhost:3000`
- Backend: `http://localhost:8080`
- Healthcheck: `http://localhost:8080/health`

## Credenciais iniciais

- `admin@hopesoft.com` / `hopesoft123`
- `operador@hopesoft.com` / `hopesoft123`

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

## Verificacao rapida apos instalar

1. Abra `http://localhost:3000`.
2. Entre com `admin@hopesoft.com`.
3. Confira se o dashboard carrega.
4. Abra a tela `Caixa` e verifique se nao ha erro de conexao.
5. Abra `Produtos` e confirme listagem.

## Problemas comuns

### Docker nao abre

- abra o Docker Desktop manualmente
- espere alguns segundos e tente iniciar novamente

### Porta ocupada

- feche outro sistema que esteja usando `3000`, `5432` ou `8080`
- ou ajuste as portas no `docker-compose.yml`

### Sistema nao responde

Na pasta `C:\HopeSoft` execute:

```powershell
docker compose logs -f
```

### Frontend abre mas nao faz login

- confirme se o backend esta respondendo em `http://localhost:8080/health`
- se necessario, pare tudo e inicie novamente

## Observacao importante

Esta instalacao e ideal para piloto local e validacao operacional. Para producao estavel, ainda e recomendado configurar backup, atualizacao controlada e monitoramento.
