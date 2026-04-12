package com.hopesoft.dto;

import com.hopesoft.model.PagamentoVenda;
import java.math.BigDecimal;

public record PagamentoVendaResponse(
        String formaPagamento,
        BigDecimal valor,
        BigDecimal valorRecebido,
        Integer parcelas,
        String codigoValeTroca
) {

    public static PagamentoVendaResponse fromEntity(PagamentoVenda pagamentoVenda) {
        return new PagamentoVendaResponse(
                pagamentoVenda.getFormaPagamento().name(),
                pagamentoVenda.getValor(),
                pagamentoVenda.getValorRecebido(),
                pagamentoVenda.getParcelas(),
                pagamentoVenda.getCodigoValeTroca()
        );
    }
}
