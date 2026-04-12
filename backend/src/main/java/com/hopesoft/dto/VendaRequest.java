package com.hopesoft.dto;

import com.hopesoft.model.FormaPagamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;

public record VendaRequest(
        @NotEmpty(message = "Venda deve ter pelo menos um item")
        List<@Valid ItemVendaRequest> itens,

        FormaPagamento formaPagamento,

        @DecimalMin(value = "0.00", message = "Valor recebido nao pode ser negativo")
        @Digits(integer = 10, fraction = 2, message = "Valor recebido deve ter no maximo 2 casas decimais")
        BigDecimal valorRecebido,

        List<@Valid PagamentoVendaRequest> pagamentos,

        @DecimalMin(value = "0.00", message = "Desconto total nao pode ser negativo")
        @Digits(integer = 10, fraction = 2, message = "Desconto total deve ter no maximo 2 casas decimais")
        BigDecimal descontoValor,

        @DecimalMin(value = "0.00", message = "Acrescimo total nao pode ser negativo")
        @Digits(integer = 10, fraction = 2, message = "Acrescimo total deve ter no maximo 2 casas decimais")
        BigDecimal acrescimoValor,

        Boolean vendaEmEspera,

        String observacao,

        String senhaGerenteAutorizacao
) {

    @AssertTrue(message = "Venda finalizada precisa de forma de pagamento ou lista de pagamentos")
    public boolean isPagamentoInformadoQuandoNecessario() {
        if (Boolean.TRUE.equals(vendaEmEspera)) {
            return true;
        }

        return formaPagamento != null || (pagamentos != null && !pagamentos.isEmpty());
    }

    @AssertTrue(message = "Valor recebido legado so pode ser informado para pagamento em dinheiro")
    public boolean isValorRecebidoValidoNoModoLegado() {
        return valorRecebido == null || formaPagamento == null || formaPagamento == FormaPagamento.DINHEIRO;
    }
}
