# HopeSoft Frontend - Design System & Architecture

## Design Philosophy: Modern Professional PDV

O HopeSoft frontend adota uma abordagem **minimalista profissional** com foco em **eficiência operacional**. A interface deve ser intuitiva para operadores de caixa, mas visualmente sofisticada para impressionar clientes e investidores.

### Design Movement
**Contemporary Minimalism com Accent Funcional**: Inspirado em sistemas de gestão empresarial modernos (Stripe, Shopify Admin), combinando limpeza visual com elementos de destaque estratégicos.

### Core Principles
1. **Clareza Funcional**: Cada elemento serve um propósito claro. Sem decoração desnecessária.
2. **Hierarquia Visual Forte**: Uso de tipografia, cor e espaçamento para guiar o usuário através das tarefas.
3. **Acessibilidade Primeiro**: Contraste adequado, navegação por teclado, feedback visual claro.
4. **Performance Obsessiva**: Carregamento rápido, animações suaves, sem bloqueios de UI.

### Color Philosophy
- **Primária**: Azul profundo (`#4080FF`) - confiança, segurança, tecnologia
- **Secundária**: Ciano (`#37D4CF`) - ação, interatividade, feedback positivo
- **Sucesso**: Verde (`#23C343`) - transações aprovadas, confirmações
- **Alerta**: Laranja (`#FF9A2E`) - atenção, avisos não críticos
- **Erro**: Vermelho (`#DC2626`) - ações destrutivas, erros
- **Neutro**: Cinza (`#A9AEB8`) - desabilitado, secundário
- **Fundo**: Branco limpo (`#FFFFFF`) com cinza muito claro (`#F9FAFB`) para seções

**Intenção Emocional**: Transmitir confiabilidade, modernidade e controle total sobre as operações.

### Layout Paradigm
**Dashboard com Sidebar Colapsável + Conteúdo Principal Fluido**

- **Sidebar Esquerda**: Navegação persistente, logo, menu colapsável para economizar espaço em tablets
- **Header Superior**: Usuário logado, notificações, busca rápida, tema (light/dark)
- **Conteúdo Principal**: Grades responsivas, cards com sombra suave, espaçamento generoso
- **Rodapé**: Informações de versão, suporte (em páginas de detalhe)

### Signature Elements
1. **Card com Sombra Suave**: `shadow-sm` com `border-border` - padrão para agrupar informações
2. **Badge de Status**: Cores semânticas (verde=ativo, cinza=inativo, laranja=pendente)
3. **Botão com Ícone + Texto**: Lucide React icons alinhados à esquerda, com feedback hover/active
4. **Tabela com Zebra Striping**: Linhas alternadas com hover, ações inline

### Interaction Philosophy
- **Feedback Imediato**: Toast notifications para ações (sucesso, erro, aviso)
- **Confirmação para Ações Destrutivas**: Dialog modal antes de deletar/cancelar
- **Hover States Claros**: Mudança de cor, elevação (shadow), cursor pointer
- **Loading States**: Skeleton loaders ou spinner com mensagem contextual
- **Validação em Tempo Real**: Feedback inline em formulários, sem esperar submit

### Animation Guidelines
- **Transições de Página**: Fade-in suave (200ms) ao entrar em nova rota
- **Hover de Botões**: Scale 1.02 + mudança de cor (100ms)
- **Modals/Drawers**: Slide-in do lado (300ms) com backdrop fade
- **Toasts**: Slide-up com fade-in (200ms), auto-dismiss em 3s
- **Skeleton Loaders**: Pulse suave (1.5s) enquanto carrega dados
- **Sem Animações Desnecessárias**: Priorizar performance em conexões lentas

### Typography System
- **Display (Títulos Grandes)**: `font-bold text-3xl` - títulos de página
- **Heading 1 (Seções)**: `font-bold text-2xl` - nomes de módulos
- **Heading 2 (Subseções)**: `font-semibold text-lg` - nomes de cards
- **Body (Padrão)**: `font-normal text-base` - conteúdo principal
- **Small (Secundário)**: `font-normal text-sm` - labels, hints, timestamps
- **Mono (Dados)**: `font-mono text-sm` - valores monetários, IDs, códigos

**Font Stack**: `system-ui, -apple-system, sans-serif` (padrão seguro)

---

## Architecture

### Folder Structure
```
client/
├── public/              # Favicon, robots.txt (APENAS config files)
├── src/
│   ├── pages/           # Page-level components (rotas)
│   │   ├── Login.tsx
│   │   ├── Dashboard.tsx
│   │   ├── Vendas.tsx
│   │   ├── Relatorios.tsx
│   │   └── NotFound.tsx
│   ├── components/      # Reusable UI components
│   │   ├── ui/          # shadcn/ui components
│   │   ├── layout/      # Layout wrappers (Sidebar, Header)
│   │   ├── forms/       # Form components
│   │   └── common/      # Shared components (Card, Badge, etc)
│   ├── hooks/           # Custom React hooks
│   │   ├── useAuth.ts
│   │   ├── useVendas.ts
│   │   └── useApi.ts
│   ├── contexts/        # React contexts
│   │   ├── AuthContext.tsx
│   │   └── ThemeContext.tsx
│   ├── lib/             # Utility functions
│   │   ├── api.ts       # Axios instance com interceptors
│   │   ├── validators.ts
│   │   └── formatters.ts
│   ├── App.tsx          # Routes & top-level layout
│   ├── main.tsx         # React entry point
│   └── index.css        # Global styles + design tokens
├── package.json
└── vite.config.ts
```

### API Integration Strategy
- **Base URL**: Configurável via `.env` (ex: `http://localhost:8080/api`)
- **Axios Instance**: Centralizado em `lib/api.ts` com interceptors para JWT
- **Error Handling**: Global error boundary + toast notifications
- **Loading States**: Skeleton loaders durante fetch de dados
- **Retry Logic**: Retry automático para erros de rede (3 tentativas)

### State Management
- **Auth**: React Context (simples, sem Redux)
- **Dados Globais**: React Context + useReducer se necessário
- **Dados Locais**: useState em componentes
- **Cache**: Implementar com `useMemo` e `useCallback` para otimização

---

## Component Checklist

### Layout Components
- [ ] `Sidebar` - Navegação lateral com menu colapsável
- [ ] `Header` - Barra superior com usuário, notificações, tema
- [ ] `DashboardLayout` - Wrapper para páginas autenticadas
- [ ] `AuthLayout` - Wrapper para páginas de login

### Form Components
- [ ] `FormInput` - Input com label, validação, erro
- [ ] `FormSelect` - Select customizado com shadcn
- [ ] `FormCheckbox` - Checkbox com label
- [ ] `SubmitButton` - Botão com loading state

### Data Display Components
- [ ] `DataTable` - Tabela com sorting, pagination, ações
- [ ] `Card` - Card padrão com shadow
- [ ] `Badge` - Status badges (ativo, inativo, pendente)
- [ ] `StatCard` - Card de métrica (ex: Total de Vendas)

### Feedback Components
- [ ] `Toast` - Notificações (já incluído via Sonner)
- [ ] `Modal` - Dialog confirmação (deletar, cancelar)
- [ ] `Skeleton` - Loader skeleton
- [ ] `EmptyState` - Estado vazio com ícone + mensagem

---

## Implementation Phases

### Phase 1: Setup & Auth
- [ ] Configurar Axios com interceptors JWT
- [ ] Implementar Login page
- [ ] Implementar AuthContext
- [ ] Implementar ProtectedRoute

### Phase 2: Dashboard & Layout
- [ ] Implementar Sidebar + Header
- [ ] Implementar DashboardLayout
- [ ] Implementar Dashboard page com stats
- [ ] Implementar tema claro/escuro

### Phase 3: Vendas (Core Feature)
- [ ] Implementar Vendas page
- [ ] Implementar form de nova venda
- [ ] Implementar listagem de vendas
- [ ] Implementar detalhes de venda

### Phase 4: Relatórios
- [ ] Implementar Relatórios page
- [ ] Implementar gráficos (Recharts)
- [ ] Implementar filtros de data
- [ ] Implementar export de dados

### Phase 5: Polish & Deploy
- [ ] Testes de integração
- [ ] Otimização de performance
- [ ] Documentação de API
- [ ] Deploy em produção
