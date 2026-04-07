# 📦 HOPESOFT - MANUAL DE INSTALAÇÃO PARA CLIENTES

> Guia completo para instalar e usar o HopeSoft na sua loja

**Versão**: 1.0  
**Data**: 2026-04-07  
**Suporte**: suporte@hopesoft.com

---

## 📋 ÍNDICE

1. [Visão Geral](#visão-geral)
2. [Pré-requisitos](#pré-requisitos)
3. [Instalação](#instalação)
4. [Primeiro Uso](#primeiro-uso)
5. [Troubleshooting](#troubleshooting)
6. [Suporte](#suporte)

---

## 🎯 VISÃO GERAL

**HopeSoft** é um sistema de frente de caixa para gerenciar vendas, produtos e estoque da sua loja.

### Funcionalidades Principais
- ✅ Registrar vendas
- ✅ Gerenciar produtos e estoque
- ✅ Relatórios diários
- ✅ Múltiplas formas de pagamento
- ✅ Cálculo automático de troco
- ✅ Autenticação segura

---

## 📋 PRÉ-REQUISITOS

### Hardware Mínimo
- **CPU**: Processador moderno (Intel i3 ou equivalente)
- **RAM**: 4GB mínimo, 8GB recomendado
- **HD**: 50GB de espaço livre
- **Internet**: Conexão estável

### Software Necessário
- **Windows 10/11** OU **Linux** OU **Mac**
- **Docker Desktop** (gerenciador de containers)
- Navegador moderno (Chrome, Firefox, Edge)

### Conectividade
- Firewall deve permitir porta **8080**
- PostgreSQL usa porta **5432** (interna, não precisa liberar)

---

## 🚀 INSTALAÇÃO (PASSO A PASSO)

### PASSO 1: Verificar Pré-requisitos

Antes de instalar, verifique se você tem:

```
□ Windows 10/11 (ou Linux/Mac)
□ Conexão de internet
□ Pelo menos 50GB livres no HD
□ Permissão para instalar programas
```

### PASSO 2: Instalar Docker Desktop

**Docker** é um programa que faz o HopeSoft rodar em um "computador virtual" seguro.

#### No Windows:

1. Acesse: https://www.docker.com/products/docker-desktop
2. Clique em **"Download for Windows"**
3. Execute o instalador (`.exe`)
4. Siga as instruções na tela
5. **Reinicie o computador** quando terminar
6. Docker Desktop vai abrir automaticamente

#### No Linux:

```bash
# Ubuntu/Debian
sudo apt-get update
sudo apt-get install docker.io docker-compose

# Iniciar Docker
sudo systemctl start docker
```

#### No Mac:

1. Baixe: https://www.docker.com/products/docker-desktop
2. Arraste para a pasta "Applications"
3. Abra Applications > Docker

### PASSO 3: Baixar HopeSoft

1. Acesse: https://github.com/seu-usuario/hopesoft
2. Clique em **"Code"** (botão verde)
3. Clique em **"Download ZIP"**
4. Descompacte em uma pasta de fácil acesso
   - Exemplo: `C:\HopeSoft` (Windows)
   - Exemplo: `/home/usuario/HopeSoft` (Linux)

### PASSO 4: Abrir Terminal/Prompt

#### Windows:
1. Procure por **"PowerShell"** ou **"Prompt de Comando"**
2. Abra como **Administrador**

#### Linux/Mac:
1. Abra o **Terminal**

### PASSO 5: Executar HopeSoft

Digite este comando e pressione **Enter**:

```bash
cd C:\HopeSoft
docker compose up --build
```

**No Linux/Mac:**
```bash
cd ~/HopeSoft
docker compose up --build
```

### PASSO 6: Aguardar Inicialização

O HopeSoft vai levar **~2-5 minutos** para inicializar na primeira vez.

Procure por estas mensagens nos logs:

```
✅ Creating hopesoft-postgres
✅ Creating hopesoft-app
✅ database system is ready
✅ Started HopesoftApplication
✅ Tomcat started on port(s): 8080
```

**IMPORTANTE**: Não feche a janela do terminal!

---

## 💻 PRIMEIRO USO

### Acessar HopeSoft

Abra seu navegador (Chrome, Firefox, Edge) e acesse:

```
http://localhost:8080/index.html
```

### Fazer Login

Use as credenciais padrão:

```
Email:  admin@hopesoft.com
Senha:  hopesoft123
```

### Primeira Tela - Caixa

Você verá a tela de **Caixa** com:
- 📦 **Produtos**: Lista de produtos à esquerda
- 🧾 **Venda Atual**: Carrinho de compras à direita

### Usar o Sistema

#### 1. Adicionar Produtos

```
1. Clique no produto na lista da esquerda
2. Produto aparece no carrinho
3. Ajuste quantidade com botões + e -
```

#### 2. Escolher Forma de Pagamento

```
1. Clique no select "Forma de Pagamento"
2. Escolha: Dinheiro, PIX, Débito ou Crédito
```

#### 3. Calcular Troco (se Dinheiro)

```
1. Se escolheu "Dinheiro"
2. Digite o "Valor Recebido"
3. Troco é calculado automaticamente
```

#### 4. Concluir Venda

```
1. Clique em "✓ Concluir Venda"
2. Confirme na janela que aparecer
3. Pronto! Venda registrada
```

### Gerenciar Produtos

Clique na aba **"📦 Produtos"** para:
- 📋 Listar produtos
- ➕ Criar novo produto
- ✏️ Editar produto
- ⚠️ Ver produtos com estoque baixo

### Ver Relatório

Clique na aba **"📊 Relatório"** para:
- 📅 Escolher uma data
- 💰 Ver total do dia
- 📊 Ver número de vendas
- 💵 Ver totais por forma de pagamento

---

## 🔄 DESLIGAR E REINICIAR

### Para Desligar (Parar Sessão)

Na janela do Terminal/PowerShell:

```
Pressione: Ctrl + C
```

### Para Reiniciar

```
cd C:\HopeSoft
docker compose up
```

**Nota**: Não coloque `--build` desta vez (será mais rápido)

---

## 🔧 TROUBLESHOOTING

### Erro 1: "Docker não está rodando"

**Problema**: Você vê mensagem como "failed to connect to docker"

**Solução**:
1. Abra **Docker Desktop**
2. Aguarde aparecer a baleia do Docker
3. Aguarde 1-2 minutos para inicializar
4. Tente novamente: `docker compose up`

---

### Erro 2: "Porta 8080 já está em uso"

**Problema**: Mensagem "bind: address already in use"

**Solução**:

**Windows**:
```bash
netstat -ano | findstr :8080
taskkill /PID <numero> /F
```

**Linux/Mac**:
```bash
lsof -ti:8080 | xargs kill -9
```

---

### Erro 3: "Não consigo acessar http://localhost:8080"

**Solução**:
1. Verifique se Docker está rodando (baleia visível)
2. Verifique nos logs se vê "Tomcat started"
3. Tente recarregar a página (F5)
4. Tente outro navegador
5. Se erro 401: Acesse `/index.html` explicitamente

---

### Erro 4: "Não consigo fazer login"

**Solução**:
1. Verifique credenciais:
   - Email: `admin@hopesoft.com`
   - Senha: `hopesoft123`
2. Tente recarregar a página (F5)
3. Limpe cache do navegador (Ctrl+Shift+Del)
4. Tente outro navegador

---

### Erro 5: "Ao criar venda, dá erro"

**Solução**:
1. Verifique se tem produtos cadastrados
2. Verifique se produtos têm estoque
3. Tente criar um novo produto primeiro
4. Tente novamente

---

## 🔐 SEGURANÇA

### Mudar Senha Padrão

Recomendamos **fortemente** mudar a senha padrão:

1. Entre no sistema com admin@hopesoft.com / hopesoft123
2. Procure por opção de "Perfil" ou "Configurações" (se disponível)
3. Mude a senha para algo seguro

**Senha segura tem**:
- ✅ Pelo menos 8 caracteres
- ✅ Mistura de letras e números
- ✅ Caracteres especiais (@, #, $, etc)

---

## 📞 SUPORTE

### Documentação Online

Acesse a documentação completa:
- 📖 Manual Geral: `docs/MANUAL_GERAL_HOPESOFT.md`
- 📋 Exemplos: `docs/EXEMPLOS_DE_PAYLOADS_API.md`

### Contato de Suporte

```
Email:    suporte@hopesoft.com
WhatsApp: +55 (83) 99999-9999
Horário:  Segunda a Sexta, 9h-18h
```

### Informações Úteis para Suporte

Se tiver problema, prepare:
1. Descrição do problema
2. Mensagem de erro exata
3. Passos para reproduzir
4. Seu sistema operacional
5. Versão do Docker

---

## 🎓 PRIMEIROS PASSOS

### Primeira Semana

```
Dia 1: Instalar e explorar a interface
Dia 2: Criar produtos de teste
Dia 3: Fazer vendas de teste
Dia 4: Ver relatórios
Dia 5: Convidar gerente para testar
```

### Checklist de Operação

- [ ] Sistema rodando sem erros
- [ ] Consegue fazer login
- [ ] Consegue criar produto
- [ ] Consegue fazer venda
- [ ] Troco funciona
- [ ] Relatório está correto

---

## 🚀 PRÓXIMOS PASSOS

### Importar Produtos Reais

Se você já tem uma lista de produtos:

1. Acesse **Produtos**
2. Clique **"➕ Novo Produto"**
3. Preencha:
   - Nome do produto
   - Preço
   - Estoque inicial
   - Categoria
4. Clique **"Salvar"**

### Criar Usuários Adicionais

Se precisar de mais usuários (caixa, gerente):

1. Abra o **Terminal** (onde Docker está rodando)
2. Você verá mensagens dos usuários criados automaticamente
3. Use as credenciais fornecidas

---

## 📝 ANOTAÇÕES

Use este espaço para anotar informações importantes:

```
Usuário Admin:     ________________________
Senha Admin:       ________________________
Data de Instalação: ________________________
Suporte Contato:   ________________________
```

---

## ✅ CONCLUSÃO

HopeSoft está instalado e pronto para usar!

### Resumo do Que Você Fez

1. ✅ Instalou Docker
2. ✅ Baixou HopeSoft
3. ✅ Executou `docker compose up --build`
4. ✅ Fez login em http://localhost:8080/index.html
5. ✅ Criou primeiro produto
6. ✅ Fez primeira venda

**Parabéns! 🎉 HopeSoft está operacional!**

---

**Precisa de ajuda?**
- 📧 Envie email para: suporte@hopesoft.com
- 💬 Mensagem WhatsApp: +55 (83) 99999-9999
- 📞 Ligue para: +55 (83) 3222-2222

**Versão**: 1.0  
**Última atualização**: 2026-04-07  

---

*HopeSoft - Sistema de Frente de Caixa para sua Loja*

