package com.hopesoft.dto;

import com.hopesoft.model.FormaPagamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record VendaRequest(
        @NotEmpty(message = "Venda deve ter pelo menos um item")
        List<@Valid ItemVendaRequest> itens,

        @NotNull(message = "Forma de pagamento e obrigatoria")
        FormaPagamento formaPagamento,

        @DecimalMin(value = "0.00", message = "Valor recebido nao pode ser negativo")
        @Digits(integer = 10, fraction = 2, message = "Valor recebido deve ter no maximo 2 casas decimais")
        BigDecimal valorRecebido
) {

    @AssertTrue(message = "Valor recebido e obrigatorio para pagamento em dinheiro")
    public boolean isValorRecebidoObrigatorioQuandoDinheiro() {
        return formaPagamento == null || formaPagamento != FormaPagamento.DINHEIRO || valorRecebido != null;
    }

    @AssertTrue(message = "Valor recebido so deve ser informado para pagamento em dinheiro")
    public boolean isValorRecebidoApenasParaDinheiro() {
        return formaPagamento == null || formaPagamento == FormaPagamento.DINHEIRO || valorRecebido == null;
    }
}
