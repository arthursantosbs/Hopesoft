package com.hopesoft.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CaixaAberturaRequest(
        @NotNull(message = "Fundo de troco inicial e obrigatorio")
        @DecimalMin(value = "0.00", message = "Fundo de troco inicial nao pode ser negativo")
        @Digits(integer = 10, fraction = 2, message = "Fundo de troco inicial deve ter no maximo 2 casas decimais")
        BigDecimal fundoTrocoInicial,

        String observacao
) {
}
