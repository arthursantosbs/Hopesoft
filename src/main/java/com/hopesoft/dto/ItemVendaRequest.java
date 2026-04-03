package com.hopesoft.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ItemVendaRequest(
        @NotNull(message = "Produto e obrigatorio")
        @Positive(message = "Produto deve ser valido")
        Long produtoId,

        @NotNull(message = "Quantidade e obrigatoria")
        @Positive(message = "Quantidade deve ser maior que zero")
        Integer quantidade
) {
}
