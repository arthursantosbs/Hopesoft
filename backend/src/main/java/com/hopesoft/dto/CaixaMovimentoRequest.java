package com.hopesoft.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CaixaMovimentoRequest(
        @NotNull(message = "Valor e obrigatorio")
        @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
        @Digits(integer = 10, fraction = 2, message = "Valor deve ter no maximo 2 casas decimais")
        BigDecimal valor,

        String observacao
) {
}
