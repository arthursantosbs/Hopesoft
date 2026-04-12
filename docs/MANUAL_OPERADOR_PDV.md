# Manual do Operador PDV

## Objetivo

Este guia foi feito para quem vai operar o caixa no teste de amanha.

## 1. Antes de comecar

1. Abra o sistema em `http://localhost:3000`.
2. Entre com um usuario autorizado.
3. Abra a tela `Caixa`.
4. Se nao houver caixa aberto, informe o fundo inicial e clique em `Abrir caixa`.

## 2. Venda normal

1. Va para a tela `Vendas`.
2. Bipar o codigo de barras ou usar `F2` para focar a busca.
3. Clique no produto para adicionar ao carrinho.
4. Ajuste quantidade ou desconto por item, se necessario.
5. Escolha a forma de pagamento.
6. Clique em `Finalizar venda`.

## 3. Pagamento misto

1. Adicione o produto ao carrinho.
2. Clique em `Adicionar forma de pagamento`.
3. Informe os valores de cada linha.
4. O total dos pagamentos precisa bater com o total da venda.
5. O troco aparece em destaque quando houver dinheiro.

Exemplo:

- `PIX` = 100,00
- `DINHEIRO` = 50,00
- `CARTAO_CREDITO` = 200,00

## 4. Venda em espera

Use quando o cliente precisar sair por alguns minutos.

1. Monte a venda normalmente.
2. Clique em `Colocar em espera`.
3. A venda vai para a area `Vendas em espera`.
4. Para retomar, clique em `Retomar no caixa`.
5. Para cancelar, clique em `Cancelar espera`.

## 5. Descontos

- O operador pode registrar descontos normais.
- Se o desconto ultrapassar o limite permitido, informe a senha do gerente no campo de autorizacao.

## 6. Vale-troca

### Gerar vale-troca

1. Na lista `Ultimas vendas`, localize a venda.
2. Clique em `Vale-troca`.
3. Informe os itens e quantidades devolvidas.
4. Se quiser, informe nome e documento do cliente.
5. Clique em `Gerar vale-troca`.

### Usar vale-troca

1. Monte a nova venda.
2. Adicione uma linha de pagamento `Vale-troca`.
3. Cole o codigo do vale.
4. Informe o valor que sera abatido.

## 7. Comprovante

1. Na lista `Ultimas vendas`, clique em `Comprovante`.
2. O texto do comprovante sera copiado.
3. Use esse conteudo na impressora ou processo de impressao disponivel no teste.

## 8. Fechamento de caixa

1. Volte para a tela `Caixa`.
2. Registre `Sangria` ou `Suprimento` sempre que acontecerem.
3. No fim do turno, conte o dinheiro.
4. Informe o valor contado em `Valor contado no fechamento`.
5. Clique em `Fechar caixa`.

## 9. Erros comuns

### Produto nao entra na venda

- confirme se o produto esta ativo
- confira se ha estoque disponivel

### Pagamento nao conclui

- confirme se a soma dos pagamentos bate com o total
- confira se o vale-troca possui saldo

### Venda em espera nao finaliza

- confira se todos os itens ainda existem
- valide se o pagamento foi informado corretamente

## 10. Atalhos e rotina recomendada

- use `F2` para buscar sem tirar a mao do teclado
- priorize bipar codigo de barras sempre que possivel
- registre sangria assim que houver muito dinheiro no caixa
- nunca feche o caixa sem conferir o valor contado
