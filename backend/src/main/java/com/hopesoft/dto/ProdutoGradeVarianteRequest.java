package com.hopesoft.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProdutoGradeVarianteRequest(
        @Size(max = 40, message = "Cor deve ter no maximo 40 caracteres")
        String cor,

        @Size(max = 20, message = "Tamanho deve ter no maximo 20 caracteres")
        String tamanho,

        @PositiveOrZero(message = "Estoque da variante nao pode ser negativo")
        Integer estoque,

        BigDecimal preco,

        String codigoBarras
) {
}
