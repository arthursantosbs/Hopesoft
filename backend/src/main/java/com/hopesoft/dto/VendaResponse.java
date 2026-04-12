package com.hopesoft.dto;

import com.hopesoft.model.FormaPagamento;
import com.hopesoft.model.Venda;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VendaResponse(
        Long id,
        Long empresaId,
        Long usuarioId,
        String usuarioNome,
        BigDecimal subtotalBruto,
        BigDecimal descontoTotal,
        BigDecimal acrescimoTotal,
        BigDecimal total,
        String formaPagamento,
        String status,
        BigDecimal troco,
        String observacao,
        LocalDateTime criadoEm,
        List<ItemVendaResponse> itens,
        List<PagamentoVendaResponse> pagamentos
) {

    public static VendaResponse fromEntity(Venda venda) {
        FormaPagamento formaPagamento = venda.getFormaPagamento();

        return new VendaResponse(
                venda.getId(),
                venda.getEmpresa().getId(),
                venda.getUsuario().getId(),
                venda.getUsuario().getNome(),
                venda.getSubtotalBruto(),
                venda.getDescontoTotal(),
                venda.getAcrescimoTotal(),
                venda.getTotal(),
                formaPagamento == null ? null : formaPagamento.name(),
                venda.getStatus().name(),
                venda.getTroco(),
                venda.getObservacao(),
                venda.getCriadoEm(),
                venda.getItens().stream()
                        .map(ItemVendaResponse::fromEntity)
                        .toList(),
                venda.getPagamentos() == null ? List.of() : venda.getPagamentos().stream()
                        .map(PagamentoVendaResponse::fromEntity)
                        .toList()
        );
    }
}
