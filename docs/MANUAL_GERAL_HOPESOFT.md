# Manual Geral HopeSoft

## 1. Visao do projeto

HopeSoft e um sistema de PDV para operacao de loja com foco em velocidade de caixa e controle pratico de estoque.

Nesta versao o produto ja cobre:

- login com JWT e perfis de acesso
- cadastro de produtos por variante
- grade de produto por cor e tamanho
- codigo de barras por variante
- alerta de estoque minimo
- venda com pagamentos mistos
- venda em espera
- vale-troca
- abertura, sangria, suprimento e fechamento de caixa
- relatorios diarios

## 2. Etapa atual do projeto

O projeto esta na fase de **MVP operacional avancado**.

Ja entregue:

- autenticacao JWT
- isolamento multiempresa nos fluxos centrais
- produtos, categorias, vendas e relatorio diario
- matriz de produtos com variantes
- impressao de etiqueta em ZPL ou EPL
- fluxo de caixa com troco, espera, desconto controlado e sessao de caixa
- frontend React operacional integrado ao mesmo repositorio do projeto
- ambiente Docker com PostgreSQL
- testes automatizados no backend

Ainda falta para uma etapa mais madura de produto:

- piloto real com loja
- deploy de producao
- backup e restore padronizados
- observabilidade e alarmes
- CI/CD
- integracao fiscal oficial com SEFAZ/SAT/MFE

## 3. Estrutura oficial do repositorio

```text
Hopesoft/
|-- backend/
|   |-- src/main/java/com/hopesoft
|   |-- src/test
|   |-- pom.xml
|   |-- mvnw
|   `-- mvnw.cmd
|-- hopesoft-frontend/
|   |-- client/src
|   |-- server
|   |-- package.json
|   |-- pnpm-lock.yaml
|   `-- vite.config.ts
|-- docs/
|   |-- EXEMPLOS_DE_PAYLOADS_API.md
|   |-- HopeSoft_API.postman_collection.json
|   |-- MANUAL_GERAL_HOPESOFT.md
|   |-- MANUAL_INSTALACAO_CLIENTES.md
|   |-- MANUAL_OPERADOR_PDV.md
|   `-- ROTEIRO_TESTE_PILOTO_AMANHA.md
|-- http/
|   `-- TESTES_HTTP_CLIENT.http
|-- scripts/
|   |-- HopeSoft-App.bat
|   |-- INICIAR_HOPESOFT.bat
|   |-- INSTALAR_HOPESOFT.bat
|   `-- PARAR_HOPESOFT.bat
|-- docker-compose.yml
`-- README.md
```

## 4. Como executar

### Opcao A - Docker

Na raiz do repositorio:

```powershell
docker compose up --build
```

Aplicacoes:

- frontend: `http://localhost:3000`
- backend: `http://localhost:8080`

### Opcao B - Execucao local

No modulo `backend`:

```powershell
.\mvnw.cmd spring-boot:run
```

No modulo `hopesoft-frontend`:

```powershell
pnpm install
pnpm dev
```

## 5. Enderecos e acesso

- Frontend: `http://localhost:3000`
- Backend: `http://localhost:8080`
- Healthcheck: `http://localhost:8080/health`
- Login: `POST /auth/login`

Usuarios iniciais:

- `admin@hopesoft.com` / `hopesoft123`
- `operador@hopesoft.com` / `hopesoft123`

## 6. Modulos entregues

### Backend

- seguranca JWT
- services e controllers de produtos, vendas, relatorios e caixa
- grade de produto e consulta por codigo de barras
- etiquetas para impressoras termicas
- vale-troca e documento fiscal interno
- tratamento de erros em JSON

### Frontend

- login integrado com JWT
- dashboard com indicadores reais
- produtos com grade, variantes e estoque baixo
- caixa PDV com scanner global, F2 para busca e pagamentos mistos
- tela de caixa para abertura, sangria, suprimento e fechamento
- relatorios diarios por data

## 7. Validacao tecnica

Comandos oficiais:

```powershell
cd backend
.\mvnw.cmd test
```

```powershell
cd hopesoft-frontend
pnpm check
pnpm build
```

Estado validado nesta rodada:

- backend com 22 testes passando
- frontend com tipagem e build de producao passando
- compose validado com backend, frontend e PostgreSQL

## 8. Observacao fiscal

O backend ja possui a estrutura para registrar documento fiscal e preparar emissao de:

- `NFC-e`
- `SAT`
- `MFE`

Nesta etapa, isso ainda e uma **base de integracao**, nao a emissao legal completa. Para operar oficialmente e preciso integrar certificado, ambiente da SEFAZ e regras do estado do cliente.

## 9. Proximos passos recomendados

Prioridade alta:

- testar em loja real
- melhorar impressao termica e acionamento de gaveta
- configurar backup automatico do banco
- definir deploy e monitoramento

Prioridade media:

- cadastro de clientes e historico de trocas
- auditoria por operador
- relatorios gerenciais mais ricos
- importacao de produtos

Prioridade futura:

- fiscal oficial por estado
- fiado estruturado
- multi-loja com visao central

## 10. Guias para a rodada de teste

- `MANUAL_INSTALACAO_CLIENTES.md` para instalar e iniciar
- `MANUAL_OPERADOR_PDV.md` para uso do caixa
- `ROTEIRO_TESTE_PILOTO_AMANHA.md` para validar o sistema em operacao
