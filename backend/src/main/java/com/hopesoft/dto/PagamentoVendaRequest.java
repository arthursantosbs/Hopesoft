package com.hopesoft.dto;

import com.hopesoft.model.FormaPagamento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record PagamentoVendaRequest(
        @NotNull(message = "Forma de pagamento e obrigatoria")
        FormaPagamento formaPagamento,

        @NotNull(message = "Valor do pagamento e obrigatorio")
        @DecimalMin(value = "0.01", message = "Valor do pagamento deve ser maior que zero")
        @Digits(integer = 10, fraction = 2, message = "Valor do pagamento deve ter no maximo 2 casas decimais")
        BigDecimal valor,

        @DecimalMin(value = "0.00", message = "Valor recebido nao pode ser negativo")
        @Digits(integer = 10, fraction = 2, message = "Valor recebido deve ter no maximo 2 casas decimais")
        BigDecimal valorRecebido,

        @Positive(message = "Parcelas deve ser maior que zero")
        Integer parcelas,

        String codigoValeTroca
) {
}
