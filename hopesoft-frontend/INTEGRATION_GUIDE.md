# HopeSoft Frontend - Integracao com Backend

## Visao geral

O frontend do HopeSoft funciona como uma aplicacao React separada do backend Spring Boot, mas consumindo a API real do sistema em todos os fluxos principais do PDV.

## Arquitetura atual

### Frontend

- local: `hopesoft-frontend/`
- desenvolvimento: `http://localhost:3000`
- build: `pnpm build`
- runtime: `node dist/index.js`

### Backend

- local: `backend/`
- API: `http://localhost:8080`
- healthcheck: `http://localhost:8080/health`

## URL da API

Use a variavel abaixo no frontend quando quiser sobrescrever o endereco padrao:

```env
VITE_API_URL=http://localhost:8080
```

Se ela nao for definida, o frontend assume `http://localhost:8080`.

## Endpoints usados pelo frontend

### Autenticacao

- `POST /auth/login`
- `GET /auth/me`

### Produtos e categorias

- `GET /produtos`
- `GET /produtos/codigo-barras/{codigo}`
- `GET /produtos/estoque/baixo`
- `POST /produtos`
- `PUT /produtos/{id}`
- `DELETE /produtos/{id}`
- `POST /produtos/grade`
- `GET /produtos/{id}/etiqueta`
- `GET /categorias`

### Vendas

- `GET /vendas?data=YYYY-MM-DD`
- `GET /vendas/em-espera`
- `POST /vendas`
- `PUT /vendas/{id}/finalizar`
- `DELETE /vendas/{id}/em-espera`
- `POST /vendas/{id}/vale-troca`
- `GET /vendas/{id}/comprovante`

### Caixa

- `GET /caixa/sessao-atual`
- `GET /caixa/historico`
- `POST /caixa/abrir`
- `POST /caixa/sangria`
- `POST /caixa/suprimento`
- `POST /caixa/fechar`

### Vales e fiscal

- `GET /vales-troca`
- `POST /fiscal/vendas/{id}/emitir?tipoDocumento=NFCE|SAT|MFE`

### Relatorios

- `GET /relatorios/dia?data=YYYY-MM-DD`

## CORS

O backend ja aceita origens locais comuns para desenvolvimento:

- `http://localhost:3000`
- `http://localhost:5173`
- `http://127.0.0.1:3000`
- `http://127.0.0.1:5173`

## Fluxo de autenticacao

1. O usuario faz login em `/auth/login`.
2. O frontend salva o JWT no `localStorage`.
3. As proximas requisicoes seguem com `Authorization: Bearer <token>`.
4. Na carga inicial, o frontend consulta `/auth/me` para validar a sessao.
5. Se o token expirar, o usuario volta para a tela de login.

## Desenvolvimento local

### Backend

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

### Frontend

```powershell
cd hopesoft-frontend
pnpm install
pnpm dev
```

## Docker

Na raiz do projeto:

```powershell
docker compose up --build
```

Servicos expostos:

- frontend em `http://localhost:3000`
- backend em `http://localhost:8080`
- banco PostgreSQL em `localhost:5432`

## Estado da integracao hoje

- login integrado com sessao JWT
- dashboard usando dados reais do backend
- produtos com grade, codigo de barras, estoque baixo e etiquetas
- caixa com venda real, venda em espera e pagamentos mistos
- troco destacado e leitura por scanner global
- gestao de caixa com abertura, sangria, suprimento e fechamento
- vale-troca, comprovante e preparacao fiscal conectados ao backend
- relatorios diarios conectados

## Ajustes futuros recomendados

- abrir gaveta e impressora termica sem depender de copiar texto manualmente
- historico paginado de vendas
- cadastro de clientes para troca nominal
- integracao fiscal oficial com SEFAZ, SAT ou MFE
