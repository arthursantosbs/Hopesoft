# Manual de Execucao Total do HopeSoft

## 1. Objetivo deste manual

Este documento foi escrito para voces dois seguirem o projeto do HopeSoft de forma organizada, mesmo sendo iniciantes.

Ele nao e um tutorial generico.

Ele e um manual de execucao real para:

- construir o sistema na ordem certa
- dividir o trabalho em dupla sem bagunca
- evitar retrabalho
- transformar o HopeSoft em um produto real para o varejo
- preparar o sistema para crescer na Paraiba

## 2. Como usar este manual

Nao tentem ler tudo correndo e depois improvisar.

O jeito certo de usar:

1. ler este manual inteiro uma vez
2. criar as tarefas no GitHub com base nele
3. executar uma fase por vez
4. so avancar quando a fase atual estiver validada
5. atualizar este documento quando a realidade do projeto mudar

Regra importante:

- nao pulem fase por ansiedade
- nao inventem funcionalidade fora da ordem
- nao codem varias coisas grandes ao mesmo tempo

## 3. Estado atual real do projeto

Hoje o projeto ja tem:

- pacote base `com.hopesoft`
- classe principal `HopesoftApplication`
- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- Spring Security
- PostgreSQL
- Lombok
- H2 para testes
- `./mvnw test` funcionando
- estrutura de pacotes pronta:
  - `config`
  - `controller`
  - `dto`
  - `exception`
  - `model`
  - `repository`
  - `security`
  - `service`
- modelos prontos:
  - `Usuario`
  - `Categoria`
  - `Produto`
  - `Venda`
  - `ItemVenda`
  - `Perfil`
  - `FormaPagamento`

Arquivos centrais que voces precisam conhecer:

- `pom.xml`
- `README.md`
- `src/main/resources/application.properties`
- `src/test/resources/application.properties`
- `src/main/java/com/hopesoft/HopesoftApplication.java`

## 3.1. Status consolidado em 2026-04-03

Resumo oficial do ponto atual do projeto:

- `Fase 0 - Fechar a fundacao`: concluida
- `Fase 1 - Decisao de multiempresa`: concluida
- `Fase 2 - Repositories`: concluida
- `Fase 3 - Seguranca e JWT`: concluida
- `Fase atual`: preparar `Fase 4 - DTOs e contratos`

Entregas tecnicas ja confirmadas:

- entidade `Empresa` integrada ao dominio principal
- repositories com consultas principais e testes de persistencia
- `JwtService`, `JwtFilter` e `SecurityConfig`
- autenticacao por email e senha com perfis `ADMIN` e `OPERADOR`
- `AuthController`, `AuthService` e tratamento JSON de erro para autenticacao

Validacao mais recente:

- comando: `.\mvnw.cmd test`
- resultado: `BUILD SUCCESS`
- total validado: `16 testes`

Documento complementar de status:

- `docs/STATUS_ATUAL_HOPESOFT.md`

## 4. Meta do produto

O HopeSoft nao deve ser tratado como trabalho de faculdade.

Meta do produto:

- criar um sistema de frente de caixa e gestao comercial
- validar esse sistema em lojas reais
- estabilizar o uso
- expandir para varias cidades
- preparar uma base para atender muitas lojas da Paraiba

Traducao pratica:

- primeiro ganhar confianca de poucos lojistas
- depois provar estabilidade
- depois expandir com disciplina

## 5. O que e sucesso na primeira fase comercial

Antes de pensar em toda a Paraiba, a primeira versao precisa provar estas coisas:

- uma loja consegue usar o sistema no dia a dia
- o login funciona
- produto pode ser cadastrado sem dor
- a venda e registrada sem travar
- o estoque baixa corretamente
- o troco funciona
- o relatorio do dia bate com o caixa
- o dono da loja quer continuar usando

Se isso ainda nao acontece, voces ainda nao tem um produto pronto para escalar.

## 6. Papéis da dupla

Como voces sao iniciantes, o segredo nao e cada um saber tudo.

O segredo e cada um ter um foco principal e revisar o outro.

### Socio A - trilha mais tecnica

Responsavel principal por:

- modelagem
- banco de dados
- repositories
- services
- seguranca
- configuracoes
- docker
- deploy

### Socio B - trilha mais voltada ao uso

Responsavel principal por:

- DTOs
- controllers
- frontend
- testes manuais
- validacao de fluxo de caixa
- documentacao de uso
- backlog funcional

### Regra obrigatoria

Mesmo com essa divisao:

- um implementa
- o outro revisa

Nao existe tarefa grande sem revisao cruzada.

## 7. Regras de trabalho em equipe

### Regras de ouro

- nunca codar direto na `main`
- nunca misturar varias funcionalidades grandes na mesma branch
- nunca deixar teste quebrado por varios dias
- nunca alterar arquivo sensivel sem avisar o outro
- nunca fazer merge sem o outro ter olhado

### Arquivos sensiveis

Estes arquivos exigem alinhamento antes de mudar:

- `pom.xml`
- `application.properties`
- configuracoes de seguranca
- classes centrais de service
- modelagem do banco

### Definition of Done

Uma tarefa so esta pronta quando:

- codigo foi escrito
- compila
- teste automatizado passou ou teste manual foi executado
- o outro socio revisou
- a documentacao minima foi atualizada

## 8. Fluxo de GitHub para iniciantes

### Estrutura de branches

- `main`: codigo mais estavel
- `develop`: integracao das entregas
- `feat/nome-curto`: nova funcionalidade
- `fix/nome-curto`: correcao
- `docs/nome-curto`: documentacao

### Fluxo diario

1. atualizar `develop`
2. criar branch da tarefa
3. fazer a tarefa
4. rodar teste
5. commitar
6. fazer push
7. abrir Pull Request para `develop`
8. o outro revisa
9. so depois fazer merge

### Comandos basicos

Atualizar a branch de integracao:

```powershell
git switch develop
git pull origin develop
```

Criar branch nova:

```powershell
git switch -c feat/repositories-iniciais
```

Ver o que mudou:

```powershell
git status
```

Adicionar alteracoes:

```powershell
git add .
```

Criar commit:

```powershell
git commit -m "feat: cria repositories iniciais"
```

Enviar para o GitHub:

```powershell
git push -u origin feat/repositories-iniciais
```

### Regra de revisao

Quem revisa deve verificar:

- se o codigo faz sentido
- se o escopo da branch esta pequeno
- se o projeto continua funcionando
- se o nome das classes e metodos esta claro
- se nao apareceu gambiarra desnecessaria

## 9. Setup obrigatorio nas duas maquinas

Cada um dos dois precisa deixar a maquina pronta.

### Checklist de ambiente

- IntelliJ IDEA aberto no projeto correto
- JDK 21 configurado
- Maven wrapper funcionando
- Git configurado com nome e email
- projeto rodando localmente
- `./mvnw test` funcionando

### Passo a passo do setup

1. instalar ou abrir IntelliJ
2. abrir a pasta do projeto HopeSoft
3. confirmar se o IntelliJ reconheceu o `pom.xml`
4. esperar baixar dependencias
5. confirmar o JDK 21 no projeto
6. abrir terminal na raiz do projeto
7. rodar:

```powershell
.\mvnw.cmd test
```

8. se o teste passar, a maquina esta apta

### O que fazer se der erro

Se `mvnw test` falhar:

- verificar `java -version`
- verificar `javac -version`
- verificar se o IntelliJ esta usando JDK e nao JRE
- verificar se abriu a pasta certa do projeto

## 10. Ordem oficial de execucao do projeto

Esta e a ordem que voces devem seguir.

1. fechar a fundacao do projeto
2. decidir multiempresa
3. criar repositories
4. criar seguranca JWT
5. criar DTOs
6. criar services
7. criar controllers
8. criar tratamento global de erros
9. criar seed inicial
10. criar frontend MVP
11. configurar Docker
12. colocar em piloto real
13. estabilizar
14. preparar escala

## 11. Fase 0 - Fechar a fundacao

### Objetivo

Ter um projeto estavel para os dois trabalharem sem confusao de ambiente.

### O que precisa existir

- projeto versionado no GitHub
- branch `main`
- branch `develop`
- README minimo
- este manual
- ambiente funcional nas duas maquinas

### Tarefas

- publicar projeto no GitHub
- convidar o socio com permissao de escrita
- criar branch `develop`
- confirmar que os dois conseguem fazer clone
- confirmar que os dois conseguem rodar `./mvnw test`

### Responsabilidades

Socio A:

- garantir ambiente Java e Maven
- revisar estrutura do projeto

Socio B:

- garantir acesso GitHub
- testar clone, pull e push

### Critério de pronto

- os dois conseguem abrir o projeto
- os dois conseguem rodar teste
- os dois conseguem trabalhar por branch

## 12. Fase 1 - Decisao de multiempresa

### Objetivo

Decidir cedo se o sistema vai nascer com suporte a varias empresas.

### Recomendacao

A recomendacao mais profissional e:

- criar a entidade `Empresa` agora

Motivo:

- voces ainda estao no inicio
- ainda ha pouca regra implementada
- isso evita uma refatoracao muito mais dolorosa depois

### Escopo minimo dessa decisao

Criar uma entidade `Empresa` e relacionar:

- `Usuario`
- `Categoria`
- `Produto`
- `Venda`

`ItemVenda` pode continuar ligado a `Venda` e `Produto`, porque herdara o contexto por relacao.

### Arquivos provaveis

- `src/main/java/com/hopesoft/model/Empresa.java`
- modelos existentes em `model`

### Tarefas

1. criar `Empresa`
2. adicionar relacao com `Usuario`
3. adicionar relacao com `Categoria`
4. adicionar relacao com `Produto`
5. adicionar relacao com `Venda`
6. revisar nomes das tabelas
7. testar se a aplicacao sobe

### Divisao da dupla

Socio A:

- modelagem e JPA

Socio B:

- revisar se a estrutura vai servir para telas futuras
- documentar como a empresa sera usada no sistema

### Critério de pronto

- entidade criada
- relacoes compilando
- teste sobe
- decisao documentada

Status em 2026-04-03: concluida

## 13. Fase 2 - Repositories

### Objetivo

Dar persistencia real ao dominio.

### Repositories a criar

- `UsuarioRepository`
- `CategoriaRepository`
- `ProdutoRepository`
- `VendaRepository`

Se a entidade `Empresa` for criada:

- `EmpresaRepository`

### Consultas minimas esperadas

`UsuarioRepository`

- buscar por email
- verificar existencia por email

`CategoriaRepository`

- verificar existencia por nome
- listar por empresa se multiempresa existir

`ProdutoRepository`

- listar ativos
- buscar por nome
- buscar produtos com estoque baixo
- listar por empresa se multiempresa existir

`VendaRepository`

- buscar por intervalo de data
- ordenar por data
- somar total do dia
- filtrar por empresa se multiempresa existir

### Tarefas

1. criar interfaces dos repositories
2. adicionar metodos de consulta
3. rodar `./mvnw test`
4. criar testes simples de persistencia se necessario

### Divisao da dupla

Socio A:

- implementa repositories e consultas

Socio B:

- revisa nomes das consultas
- cria lista de consultas necessarias para frontend

### Critério de pronto

- repositories criados
- consultas minimas definidas
- projeto compila

Status em 2026-04-03: concluida

## 14. Fase 3 - Seguranca e JWT

### Objetivo

Garantir acesso seguro.

### Componentes a criar

- `JwtService`
- `JwtFilter`
- `SecurityConfig`

### Regras minimas

- login por email e senha
- perfil `ADMIN`
- perfil `OPERADOR`
- endpoints protegidos por perfil
- token JWT para requisicoes autenticadas

### Ordem de implementacao

1. criar `JwtService`
2. criar `JwtFilter`
3. criar `SecurityConfig`
4. decidir endpoints publicos
5. testar autenticacao

### Endpoints que devem ficar livres

- rota de login
- futuramente documentacao swagger, se existir

### Divisao da dupla

Socio A:

- implementa backend de seguranca

Socio B:

- cria roteiro de teste:
  - login valido
  - login invalido
  - acesso sem token
  - acesso com perfil errado

### Critério de pronto

- login retorna token
- token protege rotas
- perfis funcionam

Status em 2026-04-03: concluida

## 15. Fase 4 - DTOs e contratos

### Objetivo

Definir o idioma oficial da API.

### DTOs minimos

- `LoginRequest`
- `LoginResponse`
- `ProdutoRequest`
- `ItemVendaRequest`
- `VendaRequest`
- `RelatorioDiaResponse`

### Regra importante

Nao exponham entidade JPA diretamente em toda parte.

Entidade representa banco.

DTO representa contrato da API.

### Tarefas

1. criar DTOs de entrada
2. criar DTOs de resposta
3. adicionar validacoes com Bean Validation
4. revisar campos obrigatorios

### Divisao da dupla

Socio A:

- cria DTOs e validacoes

Socio B:

- documenta exemplos reais de payload
- organiza requests no Postman

### Critério de pronto

- API recebe payload consistente
- mensagens de validacao sao previsiveis

## 16. Fase 5 - Services e regras de negocio

### Objetivo

Centralizar a logica importante do sistema.

### Services minimos

- `AuthService`
- `ProdutoService`
- `VendaService`
- `RelatorioService`

### Regras obrigatorias

No `ProdutoService`:

- cadastrar produto
- editar produto
- listar ativos
- buscar por nome
- identificar estoque baixo
- desativar sem apagar

No `VendaService`:

- registrar venda
- validar estoque
- baixar estoque
- calcular subtotal
- calcular total
- calcular troco quando for dinheiro

No `RelatorioService`:

- total do dia
- numero de vendas
- total por forma de pagamento

### Tarefas

1. implementar `AuthService`
2. implementar `ProdutoService`
3. implementar `VendaService`
4. implementar `RelatorioService`
5. criar testes unitarios

### Divisao da dupla

Socio A:

- implementa services

Socio B:

- monta cenarios reais de teste
- valida comportamento esperado

### Critério de pronto

- regras funcionam
- estoque insuficiente gera erro
- troco funciona
- relatorio gera valores coerentes

## 17. Fase 6 - Tratamento de erros

### Objetivo

Evitar erros feios e mensagens confusas.

### Entregas

- excecoes especificas
- handler global de excecao
- mensagens claras para API

### Erros que precisam ser tratados

- usuario nao encontrado
- produto nao encontrado
- categoria nao encontrada
- estoque insuficiente
- credenciais invalidas
- payload invalido

### Critério de pronto

- a API nao devolve erro generico desnecessariamente
- o frontend consegue mostrar mensagens compreensiveis

## 18. Fase 7 - Controllers

### Objetivo

Expor a aplicacao para uso externo.

### Controllers minimos

- `AuthController`
- `ProdutoController`
- `VendaController`
- `RelatorioController`

### Endpoints minimos

`/auth/login`

- fazer login

`/produtos`

- listar
- buscar por id
- cadastrar
- atualizar
- desativar
- listar estoque baixo

`/vendas`

- registrar venda

`/relatorios/dia`

- relatorio diario

### Tarefas

1. criar controllers
2. conectar com services
3. revisar status HTTP
4. testar tudo no Postman

### Divisao da dupla

Socio A:

- implementa controllers

Socio B:

- testa a API ponta a ponta
- monta colecao Postman oficial

### Critério de pronto

- endpoints funcionam
- payloads batem com os DTOs
- status HTTP faz sentido

## 19. Fase 8 - Seed e operacao inicial

### Objetivo

Subir o sistema com um usuario inicial.

### Entregas

- `DataSeeder`
- admin inicial
- senha inicial documentada
- categorias base se fizer sentido

### Tarefas

1. criar admin padrao
2. documentar credencial inicial
3. testar primeiro login

### Critério de pronto

- o sistema sobe
- existe um admin
- a equipe consegue entrar sem configuracao manual complicada

## 20. Fase 9 - Frontend MVP

### Objetivo

Tirar a dependencia de Postman para uso basico.

### Telas minimas

- login
- caixa
- produtos
- relatorio do dia

### Fluxos minimos

Login:

- informar email e senha
- salvar token
- redirecionar para caixa

Produtos:

- listar produtos
- cadastrar produto
- editar produto
- visualizar estoque baixo

Caixa:

- selecionar produto
- informar quantidade
- escolher forma de pagamento
- calcular total
- informar valor recebido
- calcular troco
- concluir venda

Relatorio:

- mostrar total do dia
- mostrar numero de vendas
- mostrar total por forma de pagamento

### Divisao da dupla

Socio A:

- ajusta endpoints conforme as telas precisarem

Socio B:

- implementa o frontend
- valida usabilidade

### Critério de pronto

- alguem de fora consegue operar com pouca ajuda

## 21. Fase 10 - Docker e ambiente reproduzivel

### Objetivo

Fazer o sistema parar de depender da maquina do desenvolvedor.

### Entregas

- `docker-compose.yml`
- `Dockerfile`
- banco em container
- app em container

### Tarefas

1. criar compose com PostgreSQL
2. criar Dockerfile da aplicacao
3. testar subida limpa
4. documentar comandos

### Comandos esperados no futuro

```powershell
docker compose up --build
```

### Critério de pronto

- qualquer um dos dois sobe o ambiente com poucos comandos

## 22. Fase 11 - Piloto real

### Objetivo

Colocar o HopeSoft em loja real.

### Antes do piloto

Confiram se estas respostas sao "sim":

- login funciona?
- produto pode ser cadastrado?
- venda baixa estoque?
- troco funciona?
- relatorio diario funciona?
- nao trava durante uso?
- credencial inicial esta clara?

### Tarefas do piloto

1. escolher 1 loja
2. subir o sistema para ela
3. acompanhar operacao
4. anotar bugs reais
5. corrigir rapido

### Dados que voces precisam registrar

- que erro apareceu
- em qual tela
- o que o usuario tentou fazer
- se o erro bloqueou a venda
- quanto tempo demorou para corrigir

### Critério de pronto

- uma loja usando de verdade
- dono satisfeito
- fluxo de venda confiavel

## 23. Fase 12 - Preparacao para escalar na Paraiba

### Objetivo

Preparar a empresa e o sistema para crescer com menos caos.

### Itens que entram aqui

- multiempresa real
- backup automatico
- auditoria
- observabilidade
- permissao por perfil e empresa
- onboarding simples
- suporte
- processo comercial
- precificacao
- documentacao do cliente

### Funcionalidades que provavelmente vao surgir

- clientes
- fiado
- contas a receber
- relatorios melhores
- importacao de produtos
- fiscal via integracao

## 24. Plano dos primeiros 30 dias

### Semana 1

- publicar no GitHub
- convidar socio
- fechar ambiente nas duas maquinas
- criar `develop`
- decidir `Empresa` ou `Tenant`

### Semana 2

- implementar repositories
- validar consultas
- montar backlog do JWT

### Semana 3

- implementar JWT
- implementar DTOs
- iniciar services

### Semana 4

- fechar services
- criar controllers
- testar no Postman

## 25. Ritmo semanal recomendado

### Segunda

- definir meta da semana
- escolher tarefas
- atribuir responsavel e revisor

### Terca a quinta

- implementar tarefas pequenas
- fazer push diario
- abrir PR assim que a tarefa estiver madura

### Sexta

- revisar backlog
- fazer merge do que estiver pronto
- atualizar documentacao
- decidir foco da semana seguinte

## 26. Ritual diario recomendado

### Antes de comecar

- puxar `develop`
- escolher uma tarefa
- criar branch

### Antes de encerrar o dia

- rodar teste
- commitar
- fazer push
- anotar o que falta

## 27. Lista oficial de primeiros issues no GitHub

Criem estas issues:

1. Definir estrategia multiempresa
2. Criar `Empresa`
3. Criar repositories iniciais
4. Criar `JwtService`
5. Criar `JwtFilter`
6. Criar `SecurityConfig`
7. Criar DTOs iniciais
8. Criar `ProdutoService`
9. Criar `VendaService`
10. Criar `RelatorioService`
11. Criar controllers iniciais
12. Criar `DataSeeder`
13. Montar colecao Postman
14. Criar tela de login
15. Criar tela de produtos
16. Criar tela de caixa
17. Criar tela de relatorio
18. Criar Dockerfile
19. Criar docker-compose
20. Preparar piloto em loja real

## 28. Coisas que voces nao devem fazer agora

Para nao desperdicarem energia:

- nao fazer app mobile agora
- nao fazer microservico agora
- nao fazer mil modulos financeiros agora
- nao tentar atender todo tipo de comercio ao mesmo tempo
- nao inventar fiscal do zero agora
- nao buscar perfeicao visual antes do fluxo funcionar

## 29. O que voces devem priorizar sempre

- venda funcionando
- estoque funcionando
- seguranca funcionando
- relatorio confiavel
- facilidade de uso
- confiabilidade do sistema

## 30. Conclusao operacional

Se voces seguirem este manual com disciplina, o HopeSoft deixa de ser apenas uma ideia e vira um processo de construcao real.

A ordem certa e:

1. ambiente
2. estrutura
3. persistencia
4. seguranca
5. regras de negocio
6. API
7. frontend
8. piloto
9. escala

Voces nao precisam correr.

Voces precisam construir a proxima versao certa, revisar bem, validar com loja real e repetir isso com consistencia.
