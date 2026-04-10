# Status atual do HopeSoft

Resumo para alinhar documentação e links.

## Stack

- Java 21, Spring Boot 4, PostgreSQL (Docker), frontend estático em `src/main/resources/static`

## Código (pacotes Java)

Estrutura principal em `com.hopesoft`:

- `config` — Spring, Jackson, seed
- `controller` — REST
- `dto` — contratos de API
- `exception` — erros de domínio e handler global
- `handler` — tratamento REST (ex.: `RestExceptionHandler`)
- `model` — entidades JPA
- `repository` — Spring Data JPA
- `security` — JWT e Spring Security
- `service` — regras de negócio

## Documentação

Índice completo: [docs/README.md](README.md). O [README.md](../README.md) na raiz descreve o repositório e como rodar testes.

## Próximos passos (sugestão)

Ajustar este arquivo quando fechar uma fase ou release; manter os manuais em `docs/` sincronizados com o comportamento real da API.
