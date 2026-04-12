package com.hopesoft.dto;

import com.hopesoft.model.MovimentoCaixa;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimentoCaixaResponse(
        Long id,
        String tipo,
        BigDecimal valor,
        String observacao,
        String usuarioNome,
        LocalDateTime criadoEm
) {

    public static MovimentoCaixaResponse fromEntity(MovimentoCaixa movimentoCaixa) {
        return new MovimentoCaixaResponse(
                movimentoCaixa.getId(),
                movimentoCaixa.getTipo().name(),
                movimentoCaixa.getValor(),
                movimentoCaixa.getObservacao(),
                movimentoCaixa.getUsuario().getNome(),
                movimentoCaixa.getCriadoEm()
        );
    }
}
