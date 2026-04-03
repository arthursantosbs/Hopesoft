# Guia de Execucao HopeSoft

## Objetivo

Este documento substitui o passo a passo generico do projeto original e adapta a execucao para o estado atual do HopeSoft.

Ele foi escrito para:

- manter o projeto coerente com o que ja existe no repositorio
- permitir desenvolvimento em dupla sem conflito
- orientar a construcao de um produto comercial real
- preparar a base para escalar o sistema para toda a Paraiba

Leitura complementar importante:

- `docs/GUIA_GITHUB_DUPLA_INICIANTES.md`

## Estado atual do projeto

Hoje o HopeSoft ja possui:

- namespace padronizado em `com.hopesoft`
- classe principal `HopesoftApplication`
- stack base com `Java 21`, `Spring Boot 4`, `Spring Web MVC`, `Spring Data JPA`, `Spring Security`, `PostgreSQL`, `Lombok`
- ambiente de teste com `H2`
- testes Maven funcionando com `./mvnw test`
- estrutura de pacotes pronta:
  - `config`
  - `controller`
  - `dto`
  - `exception`
  - `model`
  - `repository`
  - `security`
  - `service`
- modelos iniciais ja criados:
  - `Usuario`
  - `Categoria`
  - `Produto`
  - `Venda`
  - `ItemVenda`
  - `Perfil`
  - `FormaPagamento`

## Status consolidado em 2026-04-03

Status das fases deste guia:

- `Fase 0 - Fundacao do produto`: concluida
- `Fase 1 - Persistencia e dominio`: concluida
- `Fase 2 - Seguranca e acesso`: concluida
- `Proxima fase`: `Fase 3 - DTOs e contratos da API`

Entregas que ja estao prontas no codigo:

- entidade `Empresa` integrada ao dominio principal
- repositories com consultas principais e cobertura de persistencia
- login JWT com perfis `ADMIN` e `OPERADOR`
- `AuthController`, `AuthService` e tratamento JSON de erro para autenticacao
- `.\mvnw.cmd test` validando o projeto com `16` testes verdes

## Meta de negocio

O HopeSoft nao deve ser tratado como um projeto academico. O alvo e virar um produto comercial forte para o varejo da Paraiba.

Meta de longo prazo:

- ser o sistema de frente de caixa e gestao de referencia para lojas do estado
- comecar pequeno, validar rapido e expandir por confianca e indicacao
- construir um produto simples de operar, barato de manter e forte em suporte

Traducao pratica da meta:

1. primeiro vender para negocios reais
2. depois estabilizar operacao
3. so entao expandir cidade por cidade
4. so depois disso ampliar cobertura estadual com produto, suporte e comercial

## Principios de execucao

### 1. Produto real acima de tutorial

O guia antigo era bom para sair do zero. Agora o foco e construir um produto real, entao cada passo precisa considerar:

- operacao de loja real
- erros de usuario
- queda de internet
- seguranca
- suporte
- escalabilidade

### 2. Desenvolvimento em dupla com ownership claro

Vocês vao produzir melhor se cada etapa tiver um responsavel principal.

Sugestao de divisao:

- Socio A: backend, modelagem, banco, seguranca, infraestrutura
- Socio B: frontend, fluxo de caixa, UX, testes manuais, documentacao comercial

Isso nao impede revisao cruzada. Significa apenas que cada frente tem um dono.

### 3. Toda funcionalidade precisa nascer com validacao

Nenhuma entrega deve ser considerada pronta sem:

- regra de negocio implementada
- teste automatizado ou teste manual documentado
- endpoint ou tela validada
- criterio claro de aceite

### 4. Pensar pequeno para vender, mas pensar certo para escalar

Se a meta e atender a Paraiba inteira, algumas decisoes precisam nascer cedo:

- separar dados por empresa
- registrar auditoria minima
- ter backup
- preparar controle de usuario e permissao
- evitar codigo acoplado demais ao primeiro cliente

## Ajustes de estrategia antes de continuar

Antes de avancar forte em repositories e services, existe uma recomendacao importante:

### Introduzir conceito de empresa/tenant cedo

O projeto atual ainda esta no dominio basico de caixa. Para crescer para varias lojas, o sistema deve nascer com nocao de empresa.

Recomendacao:

- criar entidade `Empresa` ou `Tenant`
- relacionar `Usuario`, `Produto`, `Categoria`, `Venda` e `ItemVenda` ao tenant certo quando isso fizer sentido
- nunca deixar consulta de negocio sem contexto de empresa

Motivo:

- isso evita misturar dados de lojas diferentes
- facilita multiempresa e planos SaaS
- reduz uma futura refatoracao dolorosa

Se voces nao quiserem implementar multiempresa agora, pelo menos reservem essa decisao como proximo ajuste estrutural antes do piloto comercial.

## Roadmap reformulado do HopeSoft

## Fase 0 - Fundacao do produto

Status: concluida

Ja foi feito:

- organizacao do projeto
- padronizacao do namespace
- configuracao de Java/Maven
- configuracao base de banco
- criacao dos modelos iniciais
- setup de teste com H2

Objetivo para fechar esta fase:

- deixar o projeto compilando com consistencia na maquina dos dois
- alinhar convenções de branches, commits e revisao

Checklist:

- IntelliJ dos dois usando JDK 21
- `./mvnw test` funcionando nas duas maquinas
- git com fluxo simples definido
- README e este guia acessiveis para ambos

Divisao sugerida:

- Socio A: validar ambiente, Maven, banco local, Docker futuro
- Socio B: validar IntelliJ, documentacao interna e checklist de onboarding

## Fase 1 - Persistencia e dominio

Status: concluida

Objetivo:

transformar os modelos ja existentes em base de dados funcional

Entregas:

- criar `UsuarioRepository`
- criar `CategoriaRepository`
- criar `ProdutoRepository`
- criar `VendaRepository`
- revisar nomes de tabelas e colunas
- decidir se `Empresa/Tenant` entra agora

Criterio de pronto:

- repositories criados
- consultas basicas funcionando
- testes simples de persistencia passando

Divisao em dupla:

- Socio A: repositories, validacao de JPA, relacionamentos, tenant strategy
- Socio B: mapear consultas necessarias para telas e operacao real da loja

## Fase 2 - Seguranca e acesso

Status: concluida

Objetivo:

garantir que o sistema tenha login real e base de autorizacao

Entregas:

- `JwtService`
- `JwtFilter`
- `SecurityConfig`
- fluxo de autenticacao com email e senha
- perfis `ADMIN` e `OPERADOR`

Criterio de pronto:

- login retorna token
- endpoints protegidos respeitam perfil
- operador nao acessa area administrativa

Divisao em dupla:

- Socio A: backend de seguranca
- Socio B: fluxo de login no frontend e roteiro de teste de acesso

## Fase 3 - DTOs e contratos da API

Objetivo:

impedir que controllers exponham entidades cruas e consolidar os contratos do sistema

Entregas:

- `LoginRequest`
- `LoginResponse`
- `ProdutoRequest`
- `VendaRequest`
- `ItemVendaRequest`
- `RelatorioDiaResponse`
- possiveis DTOs extras de resposta para produto e venda

Criterio de pronto:

- API com payloads consistentes
- validacao com Bean Validation
- erros de entrada tratados de forma previsivel

Divisao em dupla:

- Socio A: DTOs, validacoes, padrao de resposta
- Socio B: documentar exemplos de payload no Postman ou arquivo de apoio

## Fase 4 - Services e regras de negocio

Objetivo:

colocar as regras importantes no backend e parar de depender de logica espalhada

Entregas:

- `AuthService`
- `ProdutoService`
- `VendaService`
- `RelatorioService`
- tratamento de estoque insuficiente
- calculo de troco
- busca por nome
- estoque baixo
- relatorio diario

Criterio de pronto:

- venda baixa estoque corretamente
- venda falha quando nao ha estoque
- relatorio do dia retorna total e quantidade
- regras principais cobertas por teste

Divisao em dupla:

- Socio A: services e testes unitarios
- Socio B: casos de uso reais, cenarios de erro, validacao com roteiro funcional

## Fase 5 - Controllers e API utilizavel

Objetivo:

expor o backend de forma limpa para frontend, testes manuais e futura integracao

Entregas:

- `AuthController`
- `ProdutoController`
- `VendaController`
- `RelatorioController`
- tratamento global de excecoes
- respostas HTTP coerentes

Criterio de pronto:

- Postman cobre os principais fluxos
- erros retornam mensagens legiveis
- endpoints principais estao estaveis

Divisao em dupla:

- Socio A: controllers, exceptions, status HTTP
- Socio B: colecao Postman, validacao ponta a ponta, checklist de API

## Fase 6 - Dados iniciais e operacao minima

Objetivo:

deixar o sistema pronto para subir e ser testado por uma loja

Entregas:

- `DataSeeder` para admin inicial
- cadastro base de categorias
- documentacao de acesso inicial
- organizacao minima do fluxo de uso

Criterio de pronto:

- sistema sobe com um usuario admin
- equipe consegue entrar e operar

Divisao em dupla:

- Socio A: seed e inicializacao
- Socio B: documentacao operacional de primeiro acesso

## Fase 7 - Frontend do MVP

Objetivo:

tirar o sistema da dependencia de Postman e colocar operacao na mao do usuario

Entregas minimas:

- tela de login
- tela de caixa
- tela de produtos
- tela de relatorio do dia
- utilitario JS para chamadas autenticadas

Recomendacao:

como o projeto ainda esta em fase inicial, voces podem manter frontend simples no comeco. O importante e fluxo, clareza e velocidade.

Criterio de pronto:

- um operador consegue vender sem apoio tecnico
- admin consegue cadastrar produto
- relatorio do dia fica acessivel

Divisao em dupla:

- Socio A: ajustes de API conforme necessidade real das telas
- Socio B: implementacao da interface e teste com usuarios proximos

## Fase 8 - Infraestrutura e confiabilidade

Objetivo:

evitar que o projeto vire algo que so roda na maquina do desenvolvedor

Entregas:

- `docker-compose.yml`
- `Dockerfile`
- banco em container
- app em container
- variaveis de ambiente documentadas
- processo de subida padrao

Criterio de pronto:

- novo ambiente sobe com poucos comandos
- time consegue reproduzir erro e ambiente

Divisao em dupla:

- Socio A: Docker, banco, configuracao
- Socio B: teste do passo a passo em maquina limpa e documentacao

## Fase 9 - Piloto comercial

Objetivo:

colocar o HopeSoft em uma ou poucas lojas reais e aprender rapido

Entregas:

- instalar ou subir para um cliente piloto
- acompanhar operacao diaria
- registrar bugs e pedidos
- medir tempo de atendimento e falhas reais

Criterio de pronto:

- ao menos 1 loja usando
- bugs criticos corrigidos
- fluxo de venda considerado confiavel

## Fase 10 - Preparacao para escalar na Paraiba

Objetivo:

parar de pensar como software interno e pensar como produto regional

Entregas estrategicas:

- multiempresa real
- backup automatico
- logs de auditoria
- permissao por perfil e empresa
- onboarding simples
- suporte com SLA minimo
- documentacao comercial
- precificacao
- rotina de deploy segura

Entregas de produto que vao ganhar peso:

- historico de cliente
- contas a receber
- controle de fiado
- relatorios melhores
- importacao de produtos
- NFC-e / NF-e via integracao terceirizada

## Como trabalhar em dupla sem se atrapalhar

Se voces ainda sao iniciantes com Git e GitHub, usem junto deste documento o guia:

- `docs/GUIA_GITHUB_DUPLA_INICIANTES.md`

## Ritual semanal

Toda semana:

1. definir meta da semana
2. quebrar em tarefas pequenas
3. atribuir dono principal para cada tarefa
4. revisar o que foi entregue
5. atualizar backlog

## Regra de ownership

Cada tarefa deve ter:

- um responsavel principal
- um revisor
- um criterio de aceite

## Regra de branches

Sugestao:

- `main`: estavel
- `develop`: integracao
- `feat/nome-curto`
- `fix/nome-curto`

## Regra de entrega

Uma tarefa so entra como concluida quando:

- codigo foi implementado
- o outro socio revisou
- o fluxo foi testado
- a documentacao minima foi atualizada

## Backlog imediato recomendado

Ordem sugerida para os proximos passos reais no HopeSoft a partir do estado atual:

1. fechar DTOs e contratos da API
2. implementar services de produto, venda e relatorio
3. expor controllers principais
4. criar seed do admin inicial
5. montar colecao Postman oficial
6. criar frontend minimo
7. preparar Docker
8. rodar piloto com loja real

## Divisao pratica do backlog imediato

Se quiserem comecar ja em paralelo:

### Frente do Socio A

- DTOs e validacoes
- services
- configuracao de banco e docker

### Frente do Socio B

- documentacao de payloads
- colecao Postman
- rascunho de telas
- tela de produtos
- tela de caixa
- cenarios de teste manual

### Pontos de sincronizacao obrigatorios

- depois dos repositories
- depois da seguranca
- depois dos DTOs
- antes de iniciar frontend
- antes de colocar em cliente real

## O que vai diferenciar o HopeSoft na Paraiba

Se voces querem chegar no estado inteiro, nao basta ter CRUD.

Os diferenciais mais fortes para o inicio sao:

- simplicidade de uso
- suporte rapido e humano
- implantacao facil
- preco acessivel
- confiabilidade no caixa
- boa experiencia para pequenos varejistas

Mais tarde, os diferenciais tecnicos e comerciais devem incluir:

- multiempresa
- fiscal
- relatorios gerenciais
- integracoes
- app ou painel mobile
- operacao estavel em volume maior

## Definicao de sucesso da primeira versao

A primeira versao nao precisa dominar a Paraiba. Ela precisa provar que merece escalar.

Primeiro sucesso real:

- 1 loja usando todo dia
- 10 vendas seguidas sem erro
- estoque baixando corretamente
- login e permissao funcionando
- relatorio diario confiavel
- responsavel da loja querendo continuar com o sistema

## Conclusao

O HopeSoft ja saiu da fase de projeto cru e agora precisa ser tratado como produto.

O caminho certo para atingir a Paraiba inteira e:

- comecar com base tecnica organizada
- validar com loja real
- construir confianca
- repetir com disciplina

Voces nao precisam construir tudo agora. Precisam construir a proxima versao certa.
