package com.hopesoft.dto;

import com.hopesoft.model.Venda;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VendaResponse(
        Long id,
        Long empresaId,
        Long usuarioId,
        String usuarioNome,
        BigDecimal total,
        String formaPagamento,
        BigDecimal troco,
        LocalDateTime criadoEm,
        List<ItemVendaResponse> itens
) {

    public static VendaResponse fromEntity(Venda venda) {
        return new VendaResponse(
                venda.getId(),
                venda.getEmpresa().getId(),
                venda.getUsuario().getId(),
                venda.getUsuario().getNome(),
                venda.getTotal(),
                venda.getFormaPagamento().name(),
                venda.getTroco(),
                venda.getCriadoEm(),
                venda.getItens().stream()
                        .map(ItemVendaResponse::fromEntity)
                        .toList()
        );
    }
}
