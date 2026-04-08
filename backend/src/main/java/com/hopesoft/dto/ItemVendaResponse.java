package com.hopesoft.dto;

import com.hopesoft.model.ItemVenda;
import java.math.BigDecimal;

public record ItemVendaResponse(
        Long produtoId,
        String produtoNome,
        Integer quantidade,
        BigDecimal precoUnit,
        BigDecimal subtotal
) {

    public static ItemVendaResponse fromEntity(ItemVenda itemVenda) {
        return new ItemVendaResponse(
                itemVenda.getProduto().getId(),
                itemVenda.getProduto().getNome(),
                itemVenda.getQuantidade(),
                itemVenda.getPrecoUnit(),
                itemVenda.getSubtotal()
        );
    }
}
