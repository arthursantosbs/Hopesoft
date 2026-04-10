# Status atual do HopeSoft (resumo)

## Stack
- Java 21, Spring Boot 4
- PostgreSQL via Docker
- Frontend estático em `src/main/resources/static`

## Código (pacotes Java)
- `config` — Spring, Jackson e seed
- `controller` — REST
- `dto` — contratos de API
- `exception` e `handler` — erros e tratamento global
- `model` — entidades JPA
- `repository` — persistência JPA
- `security` — JWT e Spring Security
- `service` — regras de negócio

## Próximos passos (visão)
- Consolidar DTOs e contratos da API
- Completar services e endpoints (produtos, vendas, relatórios) conforme evolução do projeto

