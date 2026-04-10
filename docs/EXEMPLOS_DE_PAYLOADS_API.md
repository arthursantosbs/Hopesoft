# Exemplos de Payloads da API

Base local: `http://localhost:8080`

## Healthcheck

```http
GET /health
```

## Login

```json
POST /auth/login
{
  "email": "admin@hopesoft.com",
  "senha": "hopesoft123"
}
```

## Usuario autenticado

```http
GET /auth/me
Authorization: Bearer {TOKEN}
```

## Categorias

```http
GET /categorias
Authorization: Bearer {TOKEN}
```

## Criar produto

```json
POST /produtos
Authorization: Bearer {TOKEN}
{
  "nome": "Cafe Premium 500g",
  "codigoBarras": "7891234567890",
  "preco": 12.50,
  "estoque": 20,
  "estoqueMin": 5,
  "categoriaId": 1,
  "ativo": true
}
```

## Atualizar produto

```json
PUT /produtos/1
Authorization: Bearer {TOKEN}
{
  "nome": "Cafe Premium 500g - Atualizado",
  "codigoBarras": "7891234567890",
  "preco": 14.50,
  "estoque": 25,
  "estoqueMin": 5,
  "categoriaId": 1,
  "ativo": true
}
```

## Buscar produtos

```http
GET /produtos
GET /produtos/1
GET /produtos/buscar?nome=cafe
GET /produtos/estoque/baixo
Authorization: Bearer {TOKEN}
```

## Desativar produto

```http
DELETE /produtos/1
Authorization: Bearer {TOKEN}
```

## Registrar venda em dinheiro

```json
POST /vendas
Authorization: Bearer {TOKEN}
{
  "itens": [
    { "produtoId": 1, "quantidade": 2 },
    { "produtoId": 2, "quantidade": 1 }
  ],
  "formaPagamento": "DINHEIRO",
  "valorRecebido": 50.00
}
```

## Registrar venda em PIX

```json
POST /vendas
Authorization: Bearer {TOKEN}
{
  "itens": [
    { "produtoId": 1, "quantidade": 1 }
  ],
  "formaPagamento": "PIX"
}
```

## Registrar venda em cartao

```json
POST /vendas
Authorization: Bearer {TOKEN}
{
  "itens": [
    { "produtoId": 1, "quantidade": 1 }
  ],
  "formaPagamento": "CARTAO_DEBITO"
}
```

```json
POST /vendas
Authorization: Bearer {TOKEN}
{
  "itens": [
    { "produtoId": 1, "quantidade": 1 }
  ],
  "formaPagamento": "CARTAO_CREDITO"
}
```

## Relatorio diario

```http
GET /relatorios/dia?data=2026-04-10
Authorization: Bearer {TOKEN}
```

## Erros importantes

- `400` payload invalido, estoque insuficiente ou valor recebido menor que o total
- `401` token ausente/invalido ou login incorreto
- `404` entidade fora do contexto da empresa
