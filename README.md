# HopeSoft

Sistema PDV com backend Spring Boot, frontend React e ambiente Docker preparado para desenvolvimento e operacao local.

## Status atual

- backend operacional com JWT, multiempresa, caixa, vale-troca e baixa de estoque por variante
- frontend React integrado ao repositorio com login, dashboard, caixa, produtos, relatorios e operacao pensada para teclado
- Docker alinhado para subir PostgreSQL, backend e frontend
- foco atual: polir a experiencia de uso para operacao real de balcao e preparar integracao fiscal oficial

## Estrutura do repositorio

- `backend/` API Spring Boot, seguranca, regras de negocio e testes
- `hopesoft-frontend/` aplicacao React para operacao do PDV
- `docs/` documentacao principal do projeto
- `http/` requests para testes manuais
- `scripts/` atalhos de instalacao e operacao no Windows

## Como executar

### Docker

```powershell
docker compose up --build
```

Acesse:

- frontend: `http://localhost:3000`
- backend: `http://localhost:8080`
- healthcheck: `http://localhost:8080/health`

### Local

Backend:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Frontend:

```powershell
cd hopesoft-frontend
pnpm install
pnpm dev
```

## Testes e validacoes

Backend:

```powershell
cd backend
.\mvnw.cmd test
```

Frontend:

```powershell
cd hopesoft-frontend
pnpm check
pnpm build
```

## Credenciais iniciais

- `admin@hopesoft.com` / `hopesoft123`
- `operador@hopesoft.com` / `hopesoft123`

## Recursos PDV ja entregues

- cadastro em grade com variantes por cor e tamanho
- codigo de barras por variante com geracao automatica
- alerta de estoque minimo por variante
- geracao de etiqueta termica em ZPL ou EPL
- venda com pagamentos mistos, troco e venda em espera
- politica de desconto com autorizacao de gerente
- abertura, sangria, suprimento e fechamento de caixa
- vale-troca com saldo reaproveitavel na proxima compra
- comprovante termico em texto para impressao rapida

## Observacao fiscal

- a estrutura de documento fiscal ja existe no backend para `NFC-e`, `SAT` e `MFE`
- a emissao legal real ainda depende de integracao com certificado, homologacao e regras da SEFAZ do estado

## Documentacao principal

- `docs/MANUAL_GERAL_HOPESOFT.md`
- `docs/MANUAL_INSTALACAO_CLIENTES.md`
- `docs/MANUAL_OPERADOR_PDV.md`
- `docs/ROTEIRO_TESTE_PILOTO_AMANHA.md`
- `docs/PACOTE_FINAL_TESTE_LOJA.md`
- `docs/EXEMPLOS_DE_PAYLOADS_API.md`
- `docs/HopeSoft_API.postman_collection.json`
- `hopesoft-frontend/INTEGRATION_GUIDE.md`

## Proximo nivel do produto

- piloto com operacao real
- ajuste fino de fluxo de caixa e estoque
- backup e restore padronizados
- observabilidade, logs e alarmes
- pipeline CI/CD
- modulos comerciais futuros
