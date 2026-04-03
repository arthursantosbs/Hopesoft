# HopeSoft

Sistema de frente de caixa e gestao comercial construido com Spring Boot para evoluir de MVP operacional para produto comercial no varejo.

## Estado atual

Hoje o projeto ja possui:

- base multiempresa com `Empresa` relacionada a `Usuario`, `Categoria`, `Produto` e `Venda`
- repositories JPA com consultas principais e testes de persistencia
- autenticacao JWT com perfis `ADMIN` e `OPERADOR`
- endpoints de autenticacao e validacao de acesso por perfil
- suite automatizada com `16` testes passando em `.\mvnw.cmd test`

## Fase do projeto

- Pelo manual detalhado, o HopeSoft concluiu a `Fase 3 - Seguranca e JWT`
- Pelo guia resumido, o HopeSoft concluiu a `Fase 2 - Seguranca e acesso`
- A proxima etapa oficial e `DTOs e contratos da API`

Resumo oficial de status:

- [STATUS_ATUAL_HOPESOFT.md](docs/STATUS_ATUAL_HOPESOFT.md)
- [MANUAL_DE_EXECUCAO_TOTAL_HOPESOFT.md](docs/MANUAL_DE_EXECUCAO_TOTAL_HOPESOFT.md)
- [GUIA_DE_EXECUCAO_HOPESOFT.md](docs/GUIA_DE_EXECUCAO_HOPESOFT.md)

## Estrutura

- `src/main/java/com/hopesoft`: codigo principal da aplicacao
- `src/main/java/com/hopesoft/model`: entidades e enums do dominio
- `src/main/java/com/hopesoft/repository`: persistencia e consultas JPA
- `src/main/java/com/hopesoft/security`: autenticacao JWT e integracao com Spring Security
- `src/main/java/com/hopesoft/controller`: endpoints HTTP
- `src/main/java/com/hopesoft/service`: regras de aplicacao
- `src/main/resources`: configuracoes da aplicacao
- `src/test/java/com/hopesoft`: testes automatizados

## Stack

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- Spring Security
- PostgreSQL
- H2 para testes
- Lombok

## Endpoints atuais

- `POST /auth/login`
- `GET /auth/me`
- `GET /admin/ping`
- `GET /operador/ping`

## Variaveis de ambiente

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SERVER_PORT`
- `APP_JWT_SECRET`
- `APP_JWT_EXPIRATION_MS`

## Validacao

Para validar o estado atual do projeto:

```powershell
.\mvnw.cmd test
```

## Proximos passos

1. Fechar DTOs de produto, venda e relatorio.
2. Implementar `ProdutoService`, `VendaService` e `RelatorioService`.
3. Expor controllers de produto, venda e relatorio.
4. Criar `DataSeeder` com usuario admin inicial.
5. Preparar colecao Postman e seed de operacao minima.
