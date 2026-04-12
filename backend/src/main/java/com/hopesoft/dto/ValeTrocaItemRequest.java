package com.hopesoft.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ValeTrocaItemRequest(
        @NotNull(message = "Produto da devolucao e obrigatorio")
        @Positive(message = "Produto da devolucao deve ser valido")
        Long produtoId,

        @NotNull(message = "Quantidade devolvida e obrigatoria")
        @Positive(message = "Quantidade devolvida deve ser maior que zero")
        Integer quantidade
) {
}
