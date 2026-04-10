# Exemplos de Payloads da API

Atualizado em `2026-04-03`.

Este arquivo resume os contratos definidos na fase de DTOs do HopeSoft.

## Login

### Request
```json
{
  "email": "admin@hopesoft.com",
  "senha": "123456"
}
```

### Response
```json
{
  "token": "jwt-aqui",
  "tipoToken": "Bearer",
  "expiraEmSegundos": 3600,
  "usuario": {
    "id": 1,
    "empresaId": 1,
    "nome": "Administrador",
    "email": "admin@hopesoft.com",
    "perfil": "ADMIN"
  }
}
```

## Produto

### Request
```json
{
  "nome": "Cafe 500g",
  "codigoBarras": "7891234567890",
  "preco": 12.50,
  "estoque": 20,
  "estoqueMin": 5,
  "categoriaId": 2,
  "ativo": true
}
```

### Response
```json
{
  "id": 10,
  "empresaId": 1,
  "nome": "Cafe 500g",
  "codigoBarras": "7891234567890",
  "preco": 12.50,
  "estoque": 20,
  "estoqueMin": 5,
  "estoqueBaixo": false,
  "ativo": true,
  "categoria": {
    "id": 2,
    "nome": "Mercearia"
  }
}
```

## Venda

### Request
```json
{
  "itens": [
    {
      "produtoId": 10,
      "quantidade": 2
    },
    {
      "produtoId": 11,
      "quantidade": 1
    }
  ],
  "formaPagamento": "DINHEIRO",
  "valorRecebido": 50.00
}
```

### Response
```json
{
  "id": 35,
  "empresaId": 1,
  "usuarioId": 4,
  "usuarioNome": "Caixa 1",
  "total": 31.00,
  "formaPagamento": "DINHEIRO",
  "troco": 19.00,
  "criadoEm": "2026-04-03T15:00:00",
  "itens": [
    {
      "produtoId": 10,
      "produtoNome": "Cafe 500g",
      "quantidade": 2,
      "precoUnit": 12.50,
      "subtotal": 25.00
    },
    {
      "produtoId": 11,
      "produtoNome": "Bolo simples",
      "quantidade": 1,
      "precoUnit": 6.00,
      "subtotal": 6.00
    }
  ]
}
```

## Relatorio do dia

### Response
```json
{
  "data": "2026-04-03",
  "total": 190.00,
  "quantidadeVendas": 12,
  "totaisPorFormaPagamento": [
    {
      "formaPagamento": "PIX",
      "total": 100.00
    },
    {
      "formaPagamento": "DINHEIRO",
      "total": 90.00
    }
  ]
}
```

## Regras de validação
- `ProdutoRequest` exige nome, preco, estoque e estoque mínimo válidos.
- `ItemVendaRequest` exige `produtoId` e `quantidade` maiores que zero.
- `VendaRequest` exige pelo menos um item.
- `valorRecebido` é obrigatório quando `formaPagamento` for `DINHEIRO`.
- `valorRecebido` não deve ser informado para formas de pagamento diferentes de `DINHEIRO`.

