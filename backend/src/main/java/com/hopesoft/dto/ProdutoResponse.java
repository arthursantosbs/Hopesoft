package com.hopesoft.dto;

import com.hopesoft.model.Produto;
import java.math.BigDecimal;

public record ProdutoResponse(
        Long id,
        Long empresaId,
        String nome,
        String modelo,
        String referencia,
        String gradeGrupo,
        String cor,
        String tamanho,
        String codigoBarras,
        BigDecimal preco,
        Integer estoque,
        Integer estoqueMin,
        boolean estoqueBaixo,
        boolean ativo,
        CategoriaResponse categoria
) {

    public static ProdutoResponse fromEntity(Produto produto) {
        return new ProdutoResponse(
                produto.getId(),
                produto.getEmpresa().getId(),
                produto.getNome(),
                produto.getModelo(),
                produto.getReferencia(),
                produto.getGradeGrupo(),
                produto.getCor(),
                produto.getTamanho(),
                produto.getCodigoBarras(),
                produto.getPreco(),
                produto.getEstoque(),
                produto.getEstoqueMin(),
                produto.getEstoque() <= produto.getEstoqueMin(),
                Boolean.TRUE.equals(produto.getAtivo()),
                CategoriaResponse.fromEntity(produto.getCategoria())
        );
    }
}
