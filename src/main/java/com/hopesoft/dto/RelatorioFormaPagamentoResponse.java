package com.hopesoft.dto;

import java.math.BigDecimal;

public record RelatorioFormaPagamentoResponse(
        String formaPagamento,
        BigDecimal total
) {
}
