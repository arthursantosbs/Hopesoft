package com.hopesoft.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RelatorioDiaResponse(
        LocalDate data,
        BigDecimal total,
        long quantidadeVendas,
        List<RelatorioFormaPagamentoResponse> totaisPorFormaPagamento
) {
}
