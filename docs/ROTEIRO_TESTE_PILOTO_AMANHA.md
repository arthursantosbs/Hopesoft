# Roteiro de Teste Piloto

## Objetivo

Este roteiro serve para validar o HopeSoft em um teste operacional real ou semi-real amanha.

## Preparacao antes de abrir a loja

1. Abrir Docker Desktop.
2. Executar `scripts\INICIAR_HOPESOFT.bat`.
3. Confirmar acesso ao frontend em `http://localhost:3000`.
4. Fazer login com `admin@hopesoft.com`.
5. Conferir se as telas `Dashboard`, `Produtos`, `Vendas`, `Caixa` e `Relatorios` abrem sem erro.

## Check de dados iniciais

1. Confirmar se existem categorias carregadas.
2. Cadastrar pelo menos uma grade de produto.
3. Confirmar que cada variante recebeu codigo de barras.
4. Verificar se ao menos um item esta com estoque suficiente para testes.

## Cenarios obrigatorios de teste

### 1. Venda simples

- vender 1 item em `PIX`
- confirmar a venda no historico

### 2. Venda com troco

- vender 1 item em `DINHEIRO`
- informar valor recebido maior
- conferir o troco destacado

### 3. Pagamento misto

- dividir uma venda entre `PIX`, `DINHEIRO` e `CARTAO_CREDITO`
- validar se o total fecha corretamente

### 4. Venda em espera

- montar uma venda
- colocar em espera
- retomar a venda
- finalizar com sucesso

### 5. Cancelamento de espera

- criar outra venda em espera
- cancelar a espera
- confirmar que ela saiu da fila

### 6. Desconto

- aplicar desconto pequeno
- depois testar desconto acima do limite do operador
- validar exigencia de senha do gerente

### 7. Vale-troca

- finalizar uma venda
- gerar vale-troca de um item
- copiar o codigo
- usar esse vale em uma nova venda

### 8. Caixa

- abrir caixa com fundo inicial
- registrar uma sangria
- registrar um suprimento
- fechar caixa informando o valor contado

### 9. Relatorio

- abrir a tela `Relatorios`
- conferir faturamento, quantidade de vendas e distribuicao por pagamento

## Resultado esperado do piloto

O sistema deve:

- permitir venda sem travas
- aceitar pagamento misto
- mostrar troco com clareza
- manter estoque coerente
- guardar vendas em espera
- registrar abertura e fechamento de caixa
- gerar vale-troca

## Itens para anotar durante o teste

- o operador conseguiu usar sem treinamento longo
- houve alguma etapa confusa
- o scanner funcionou bem
- o tempo para registrar uma venda foi aceitavel
- o troco e os pagamentos ficaram claros
- faltou algum botao rapido
- faltou impressao termica automatica

## Pendencias ja conhecidas

- emissao fiscal ainda esta em modo de preparacao, nao emissao legal completa
- comprovante e etiqueta ainda dependem de copiar o conteudo para o fluxo de impressao
- o frontend ainda esta em repositorio Git separado do principal

## Fechamento do dia

No fim do piloto, registre:

- o que funcionou bem
- o que precisa mudar antes de colocar em loja
- quais recursos ficaram prontos para a proxima rodada
