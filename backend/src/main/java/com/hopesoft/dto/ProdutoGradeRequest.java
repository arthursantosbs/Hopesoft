package com.hopesoft.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public record ProdutoGradeRequest(
        @NotBlank(message = "Nome base e obrigatorio")
        @Size(max = 120, message = "Nome base deve ter no maximo 120 caracteres")
        String nomeBase,

        @NotBlank(message = "Modelo e obrigatorio")
        @Size(max = 120, message = "Modelo deve ter no maximo 120 caracteres")
        String modelo,

        @NotBlank(message = "Referencia e obrigatoria")
        @Size(max = 80, message = "Referencia deve ter no maximo 80 caracteres")
        String referencia,

        @NotNull(message = "Preco base e obrigatorio")
        @DecimalMin(value = "0.01", message = "Preco base deve ser maior que zero")
        @Digits(integer = 10, fraction = 2, message = "Preco base deve ter no maximo 2 casas decimais")
        BigDecimal precoBase,

        @NotNull(message = "Estoque minimo e obrigatorio")
        @PositiveOrZero(message = "Estoque minimo nao pode ser negativo")
        Integer estoqueMin,

        Long categoriaId,

        @NotEmpty(message = "Informe ao menos uma cor")
        List<@NotBlank String> cores,

        @NotEmpty(message = "Informe ao menos um tamanho")
        List<@NotBlank String> tamanhos,

        @NotNull(message = "Estoque inicial padrao e obrigatorio")
        @PositiveOrZero(message = "Estoque inicial padrao nao pode ser negativo")
        Integer estoqueInicialPadrao,

        List<@Valid ProdutoGradeVarianteRequest> variantes
) {
}
