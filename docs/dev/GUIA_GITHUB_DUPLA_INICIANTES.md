# Guia GitHub em Dupla (Iniciantes) - Resumo

## Objetivo
Trabalhar em dupla sem bagunçar o repositório e sem conflitos.

## Regra de ouro
**Nunca codar direto na `main`.**  
Use branches e PR.

## Fluxo recomendado
1. Atualizar base (`develop`)
2. Criar branch da tarefa (`feat/...` ou `fix/...`)
3. Fazer mudanças pequenas e testáveis
4. Abrir Pull Request de `feat/...` → `develop`
5. Revisar, testar e manter `develop` sempre funcional

## Estratégia de branches (exemplos)
- `main`
- `develop`
- `feat/nome-da-tarefa`
- `fix/nome-do-problema`
- `docs/nome-da-doc`

## Divisão sugerida (só para evitar conflito)
- Socio A: `repository`, `service`, `security`
- Socio B: `dto`, `controller`, frontend e `docs`

## Regra para commits
- Commit pequeno (uma ideia por commit)
- Commit com mensagem clara (ex.: `feat: adiciona login JWT`)

## Arquivos sensíveis (avisos)
Tenha cuidado ao mexer em:
- `pom.xml`
- configurações centrais (ex.: properties e classes de segurança)
- arquivos “centrais” de fluxo (ex.: controllers principais)

