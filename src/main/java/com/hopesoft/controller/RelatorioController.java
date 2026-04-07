package com.hopesoft.controller;

import com.hopesoft.dto.RelatorioDiaResponse;
import com.hopesoft.dto.RelatorioFormaPagamentoResponse;
import com.hopesoft.model.Empresa;
import com.hopesoft.model.FormaPagamento;
import com.hopesoft.security.HopesoftUserDetails;
import com.hopesoft.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
        // Obter a empresa do usuário autenticado
        Empresa empresa = new Empresa();
        empresa.setId(userDetails.getEmpresaId());

        // Calcular totais
        BigDecimal total = relatorioService.totalDoDia(empresa, data);
        Long quantidadeVendas = relatorioService.numeroDeVendas(empresa, data);
        Map<FormaPagamento, BigDecimal> totaisPorFormaPagamento =
                relatorioService.totalPorFormaDePagamento(empresa, data);

        // Converter para Response
        List<RelatorioFormaPagamentoResponse> formasPagamento = totaisPorFormaPagamento
                .entrySet()
                .stream()
                .map(entry -> new RelatorioFormaPagamentoResponse(
                        entry.getKey().name(),
                        entry.getValue()
                ))
                .toList();

        RelatorioDiaResponse response = new RelatorioDiaResponse(
                data,
                total != null ? total : BigDecimal.ZERO,
                quantidadeVendas != null ? quantidadeVendas : 0L,
                formasPagamento
        );

        return ResponseEntity.ok(response);
    }
}

