package com.hopesoft.dto;

import com.hopesoft.model.ItemVenda;
import java.math.BigDecimal;

public record ItemVendaResponse(
        Long produtoId,
        String produtoNome,
        String variante,
        Integer quantidade,
        BigDecimal precoUnit,
        BigDecimal subtotalBruto,
        BigDecimal descontoValor,
        BigDecimal acrescimoValor,
        Integer quantidadeDevolvida,
        BigDecimal subtotal
) {

    public static ItemVendaResponse fromEntity(ItemVenda itemVenda) {
        String variante = String.join(
                " / ",
                itemVenda.getProduto().getCor() == null ? "" : itemVenda.getProduto().getCor(),
                itemVenda.getProduto().getTamanho() == null ? "" : itemVenda.getProduto().getTamanho()
        ).trim();

        return new ItemVendaResponse(
                itemVenda.getProduto().getId(),
                itemVenda.getProduto().getNome(),
                variante.isBlank() ? null : variante,
                itemVenda.getQuantidade(),
                itemVenda.getPrecoUnit(),
                itemVenda.getSubtotalBruto(),
                itemVenda.getDescontoValor(),
                itemVenda.getAcrescimoValor(),
                itemVenda.getQuantidadeDevolvida(),
                itemVenda.getSubtotal()
        );
    }
}
