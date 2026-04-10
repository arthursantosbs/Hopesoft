# HopeSoft

Sistema de frente de caixa com backend Spring Boot, frontend HTML/CSS/JS e ambiente Docker pronto para uso local.

## Status atual

- Backend MVP concluido e testado
- Frontend operacional concluido
- Docker e scripts Windows alinhados com a estrutura atual
- Proximo passo de produto: piloto real, observabilidade e rotina de deploy/backup

## Estrutura profissional do repositorio

- `backend/` aplicacao Spring Boot, API, autenticacao JWT, telas estaticas e testes
- `docs/` documentacao enxuta e fonte de verdade do projeto
- `http/` requests prontos para testes manuais
- `scripts/` instalacao e operacao local no Windows
- `Dockerfile` e `docker-compose.yml` para ambiente completo

## Como executar

### Docker

```powershell
docker compose up --build
```

Acesse: `http://localhost:8080`

### Local

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

## Testes

```powershell
cd backend
.\mvnw.cmd test
```

## Credenciais iniciais

- `admin@hopesoft.com` / `hopesoft123`
- `operador@hopesoft.com` / `hopesoft123`

## Documentos principais

- `docs/MANUAL_GERAL_HOPESOFT.md`
- `docs/MANUAL_INSTALACAO_CLIENTES.md`
- `docs/EXEMPLOS_DE_PAYLOADS_API.md`
- `docs/HopeSoft_API.postman_collection.json`

## O que falta para o projeto evoluir

- piloto em loja real
- rotina de backup e restore
- logs/metricas/monitoramento
- pipeline CI/CD
- funcionalidades comerciais futuras como clientes, fiado estruturado e fiscal
