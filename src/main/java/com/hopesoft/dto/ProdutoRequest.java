package com.hopesoft.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProdutoRequest(
        @NotBlank(message = "Nome do produto e obrigatorio")
        @Size(max = 120, message = "Nome do produto deve ter no maximo 120 caracteres")
        String nome,

        @Size(max = 64, message = "Codigo de barras deve ter no maximo 64 caracteres")
        String codigoBarras,

        @NotNull(message = "Preco e obrigatorio")
        @DecimalMin(value = "0.01", message = "Preco deve ser maior que zero")
        @Digits(integer = 10, fraction = 2, message = "Preco deve ter no maximo 2 casas decimais")
        BigDecimal preco,

        @NotNull(message = "Estoque e obrigatorio")
        @PositiveOrZero(message = "Estoque nao pode ser negativo")
        Integer estoque,

        @NotNull(message = "Estoque minimo e obrigatorio")
        @PositiveOrZero(message = "Estoque minimo nao pode ser negativo")
        Integer estoqueMin,

        @Positive(message = "Categoria deve ser valida")
        Long categoriaId,

        Boolean ativo
) {
}
