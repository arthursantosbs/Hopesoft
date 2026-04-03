# Guia GitHub em Dupla para Iniciantes

## Objetivo

Este guia existe para ajudar voces dois a trabalharem juntos sem baguncar o projeto.

Ele foi escrito para quem ainda esta aprendendo Git e GitHub.

## Ideia principal

Git e GitHub nao servem so para "salvar codigo".

Eles servem para:

- registrar o que foi feito
- dividir trabalho
- revisar mudancas
- evitar perder codigo
- voltar atras quando necessario

## Ferramentas recomendadas

Para iniciantes, o caminho mais leve e:

- GitHub para hospedar o repositorio
- GitHub Desktop para branch, commit, push e pull
- IntelliJ para editar codigo
- terminal para rodar `./mvnw test`

Se voces tentarem aprender tudo so pelo terminal agora, pode ficar pesado demais no inicio.

## Regra de ouro

Nunca codem direto na `main`.

Fluxo seguro:

1. `main` guarda o que esta mais estavel
2. `develop` guarda o que esta em construcao
3. cada tarefa vai para uma branch propria
4. toda branch volta por Pull Request

## Estrutura de branches recomendada

- `main`
- `develop`
- `feat/nome-curto-da-tarefa`
- `fix/nome-curto-do-problema`
- `docs/nome-curto-da-doc`

Exemplos:

- `feat/repositories-iniciais`
- `feat/login-jwt`
- `fix/calculo-troco`
- `docs/guia-onboarding`

## O fluxo mais simples para voces

### Antes de comecar uma tarefa

1. alinhem quem vai fazer o que
2. decidam qual branch cada um vai usar
3. evitem os dois mexerem no mesmo arquivo ao mesmo tempo

### Exemplo de divisao boa

- Socio A trabalha em `repository`, `service`, `security`
- Socio B trabalha em `dto`, `controller`, `frontend`, `docs`

### Exemplo de divisao ruim

- os dois alterando `ProdutoService.java` ao mesmo tempo
- os dois alterando `application.properties` sem combinar

## Fluxo diario recomendado

### Passo 1 - atualizar o projeto local

Se estiver usando terminal:

```powershell
git switch develop
git pull origin develop
```

Se estiver usando GitHub Desktop:

1. selecione a branch `develop`
2. clique em `Fetch origin`
3. clique em `Pull origin` se aparecer

### Passo 2 - criar uma branch da tarefa

Terminal:

```powershell
git switch -c feat/repositories-iniciais
```

GitHub Desktop:

1. `Current Branch`
2. `New Branch`
3. nomeie com `feat/...` ou `fix/...`

### Passo 3 - codar uma tarefa pequena

Nao tentem fazer 10 coisas na mesma branch.

Uma branch boa geralmente resolve:

- uma funcionalidade
- um bug
- uma documentacao

## Fluxo de commit para iniciantes

Commits devem ser pequenos e explicativos.

Exemplos bons:

- `feat: cria repositories iniciais`
- `feat: adiciona configuracao JWT`
- `fix: corrige calculo de troco`
- `docs: adiciona guia de trabalho em dupla`
- `test: adiciona teste de estoque insuficiente`

## Comandos mais importantes

### Ver o que mudou

```powershell
git status
```

### Adicionar arquivos para commit

```powershell
git add .
```

Melhor ainda, quando voces ja estiverem mais confortaveis:

```powershell
git add src/main/java/com/hopesoft/repository
```

### Criar commit

```powershell
git commit -m "feat: cria repositories iniciais"
```

### Enviar para o GitHub

```powershell
git push -u origin feat/repositories-iniciais
```

## Como abrir Pull Request

Depois do `push`, abram o GitHub e criem um Pull Request.

Padrao recomendado:

- base: `develop`
- compare: sua branch da tarefa

Descricao simples do PR:

- o que foi feito
- o que ainda falta
- como testar

Exemplo:

- criei `UsuarioRepository`, `ProdutoRepository`, `CategoriaRepository` e `VendaRepository`
- ainda falta adicionar testes de persistencia
- para testar, rode `./mvnw test`

## Como revisar o codigo do outro

O revisor deve olhar:

- se a ideia faz sentido
- se o codigo compila
- se o nome das coisas esta claro
- se nao quebrou o que ja existia
- se a branch esta pequena e objetiva

No comeco, revisar nao significa achar erro em tudo.

Revisar significa ajudar o outro a entregar melhor.

## Regra simples de aprovacao

Uma tarefa entra em `develop` quando:

- foi testada
- o outro socio viu
- nao existe erro obvio
- a branch tem escopo pequeno

## Como evitar conflito

As melhores formas de evitar conflito sao:

- dividir por arquivo ou modulo
- puxar atualizacao antes de comecar
- subir branch pequena
- abrir PR cedo
- conversar antes de mexer em arquivos sensiveis

Arquivos sensiveis:

- `pom.xml`
- `application.properties`
- `SecurityConfig`
- arquivos centrais de service

## Se der conflito no merge

Nao entrem em panico. Conflito e normal.

Fluxo simples:

1. pare
2. avise o outro socio
3. atualize sua branch com `develop`
4. resolvam o conflito juntos se necessario

Terminal:

```powershell
git switch develop
git pull origin develop
git switch feat/sua-branch
git merge develop
```

Se aparecer conflito:

1. abra o arquivo
2. escolha o trecho certo
3. apague as marcas de conflito
4. salve
5. rode:

```powershell
git add .
git commit -m "fix: resolve conflito com develop"
```

## Melhor estrategia para voces no comeco

Se forem iniciantes, evitem rebase agora.

Usem primeiro:

- branch
- commit
- push
- pull request
- merge

Isso ja resolve quase tudo no inicio.

## GitHub Issues para organizar o trabalho

Criem uma Issue para cada tarefa relevante.

Exemplos:

- `Criar UsuarioRepository`
- `Configurar JWT`
- `Criar tela de login`
- `Implementar venda com baixa de estoque`

Cada Issue deve ter:

- titulo claro
- descricao curta
- responsavel
- criterio de pronto

## Quadro simples no GitHub Projects

Se quiserem ficar organizados sem complicar, criem 4 colunas:

- `Backlog`
- `Em andamento`
- `Em revisao`
- `Concluido`

Toda tarefa deve estar em uma dessas colunas.

## Ritmo ideal para voces

### Todo dia

- puxar atualizacao
- fazer uma tarefa pequena
- commitar
- subir para o GitHub

### Toda semana

- revisar backlog
- decidir o foco da semana
- fechar o que esta aberto
- evitar acumular branches velhas

## O que nao fazer

- nao editar direto na `main`
- nao passar dias sem commit
- nao misturar varias funcionalidades numa branch so
- nao dar merge sem o outro olhar
- nao ignorar teste quebrado
- nao abrir PR gigante

## Sequencia recomendada para voces aprenderem sem travar

Semana 1:

- aprender `status`, `add`, `commit`, `push`, `pull`

Semana 2:

- aprender branch e pull request

Semana 3:

- aprender review e merge

Semana 4:

- aprender a resolver conflito simples

## Modelo de rotina real para o HopeSoft

Exemplo:

1. Arthur pega a tarefa `repositories`
2. Socio pega a tarefa `rascunho das telas`
3. cada um cria sua branch
4. cada um trabalha no proprio escopo
5. no fim do dia, ambos fazem push
6. quando fechar a tarefa, abrem PR para `develop`
7. o outro revisa
8. depois do merge, os dois atualizam `develop`

## Checklist antes de abrir PR

- rodei os testes?
- o projeto continua subindo?
- alterei so o que precisava?
- o nome do commit esta claro?
- escrevi o que foi feito?

## Checklist antes de dar merge

- entendi o que meu socio fez?
- testei ou li com calma?
- o PR esta pequeno o suficiente?
- nao vai quebrar a branch `develop`?

## Conclusao

Para voces, o melhor caminho e simplicidade com disciplina.

O segredo nao e dominar Git inteiro agora.

O segredo e repetir bem este ciclo:

1. criar branch
2. fazer tarefa pequena
3. commitar
4. subir
5. abrir PR
6. revisar
7. fazer merge

Se voces seguirem isso por algumas semanas, GitHub deixa de ser medo e vira ferramenta de crescimento do HopeSoft.
