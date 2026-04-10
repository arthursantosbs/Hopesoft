package com.hopesoft.controller;

import com.hopesoft.dto.RelatorioDiaResponse;
import com.hopesoft.dto.RelatorioFormaPagamentoResponse;
import com.hopesoft.security.HopesoftUserDetails;
import com.hopesoft.service.RelatorioService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/dia")
    public ResponseEntity<RelatorioDiaResponse> gerarRelatorioDia(
            @RequestParam LocalDate data,
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        BigDecimal total = relatorioService.totalDoDia(userDetails.getEmpresaId(), data);
        Long quantidadeVendas = relatorioService.numeroDeVendas(userDetails.getEmpresaId(), data);
        List<RelatorioFormaPagamentoResponse> formasPagamento =
                relatorioService.totalPorFormaDePagamento(userDetails.getEmpresaId(), data);

        RelatorioDiaResponse response = new RelatorioDiaResponse(
                data,
                total,
                quantidadeVendas,
                formasPagamento
        );

        return ResponseEntity.ok(response);
    }
}
