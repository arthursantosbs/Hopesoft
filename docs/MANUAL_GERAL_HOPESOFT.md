# Manual Geral HopeSoft

## 1. Visao do projeto

HopeSoft e um sistema de frente de caixa para pequenas operacoes comerciais. O foco desta versao e entregar um fluxo confiavel de:

- login
- cadastro e consulta de produtos
- venda com baixa de estoque
- relatorio diario
- uso local com Docker

## 2. Etapa atual do projeto

O projeto esta na fase de **MVP operacional fechado**.

Ja entregue:

- autenticacao JWT
- isolamento por empresa nas rotas centrais
- categorias, produtos, vendas e relatorio diario
- frontend MVP servindo pelo proprio backend
- scripts Windows para operacao local
- ambiente Docker com PostgreSQL
- testes automatizados do backend

Ainda falta para uma etapa mais madura de produto:

- piloto real com loja
- deploy em ambiente de producao
- backup e restore padronizados
- observabilidade e alarmes
- pipeline CI/CD
- roadmap comercial e fiscal

## 3. Estrutura oficial do repositorio

```text
Hopesoft/
|-- backend/
|   |-- src/main/java/com/hopesoft
|   |-- src/main/resources
|   |-- src/test
|   |-- pom.xml
|   |-- mvnw
|   `-- mvnw.cmd
|-- docs/
|   |-- EXEMPLOS_DE_PAYLOADS_API.md
|   |-- HopeSoft_API.postman_collection.json
|   |-- MANUAL_GERAL_HOPESOFT.md
|   `-- MANUAL_INSTALACAO_CLIENTES.md
|-- http/
|   `-- TESTES_HTTP_CLIENT.http
|-- scripts/
|   |-- HopeSoft-App.bat
|   |-- INICIAR_HOPESOFT.bat
|   |-- INSTALAR_HOPESOFT.bat
|   `-- PARAR_HOPESOFT.bat
|-- .dockerignore
|-- Dockerfile
|-- docker-compose.yml
`-- README.md
```

## 4. Como executar

### Opcao A - Docker

Na raiz do repositorio:

```powershell
docker compose up --build
```

### Opcao B - Execucao local

No modulo `backend`:

```powershell
.\mvnw.cmd spring-boot:run
```

## 5. Enderecos e acesso

- Aplicacao: `http://localhost:8080`
- Healthcheck: `http://localhost:8080/health`
- Login: `POST /auth/login`

Usuarios iniciais:

- `admin@hopesoft.com` / `hopesoft123`
- `operador@hopesoft.com` / `hopesoft123`

## 6. Validacao tecnica

Comando oficial de testes:

```powershell
cd backend
.\mvnw.cmd test
```

Estado validado nesta organizacao:

- testes do backend passando
- seed desligado nos testes
- Docker alinhado com a pasta `backend/`
- frontend sem categorias hardcoded
- venda protegida por contexto de empresa

## 7. Modulos entregues

### Backend

- seguranca JWT
- repositories, services e controllers
- tratamento de erros em JSON
- seed inicial configuravel
- relatorio diario por forma de pagamento

### Frontend

- `index.html` para login
- `caixa.html` para venda
- `produtos.html` para gestao de produtos
- `relatorio.html` para fechamento diario

## 8. Proximos passos recomendados

Prioridade alta:

- testar em operacao real
- configurar backup automatico do banco
- definir processo de deploy
- monitorar falhas e tempo de resposta

Prioridade media:

- dashboard administrativo mais completo
- historico detalhado de vendas
- auditoria por operador
- importacao de produtos

Prioridade futura:

- modulo de clientes
- fiado estruturado
- integracao fiscal
- multi-loja com administracao central
