package com.hopesoft.dto;

import com.hopesoft.model.ValeTroca;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ValeTrocaResponse(
        Long id,
        String codigo,
        BigDecimal saldoOriginal,
        BigDecimal saldo,
        String status,
        String nomeCliente,
        String documentoCliente,
        LocalDateTime criadoEm,
        LocalDateTime expiraEm,
        String observacao,
        Long vendaOrigemId
) {

    public static ValeTrocaResponse fromEntity(ValeTroca valeTroca) {
        return new ValeTrocaResponse(
                valeTroca.getId(),
                valeTroca.getCodigo(),
                valeTroca.getSaldoOriginal(),
                valeTroca.getSaldo(),
                valeTroca.getStatus().name(),
                valeTroca.getNomeCliente(),
                valeTroca.getDocumentoCliente(),
                valeTroca.getCriadoEm(),
                valeTroca.getExpiraEm(),
                valeTroca.getObservacao(),
                valeTroca.getVendaOrigem().getId()
        );
    }
}
