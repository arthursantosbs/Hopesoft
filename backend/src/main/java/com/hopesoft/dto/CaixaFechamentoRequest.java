package com.hopesoft.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CaixaFechamentoRequest(
        @NotNull(message = "Valor informado no fechamento e obrigatorio")
        @DecimalMin(value = "0.00", message = "Valor informado no fechamento nao pode ser negativo")
        @Digits(integer = 10, fraction = 2, message = "Valor informado no fechamento deve ter no maximo 2 casas decimais")
        BigDecimal valorInformadoFechamento,

        String observacao
) {
}
