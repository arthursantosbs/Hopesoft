package com.hopesoft.dto;

import com.hopesoft.model.CaixaSessao;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CaixaSessaoResponse(
        Long id,
        String status,
        BigDecimal fundoTrocoInicial,
        BigDecimal valorInformadoFechamento,
        BigDecimal valorEsperadoFechamento,
        BigDecimal totalSangrias,
        BigDecimal totalSuprimentos,
        BigDecimal totalVendasDinheiro,
        BigDecimal totalVendasPix,
        BigDecimal totalVendasCartao,
        BigDecimal totalVendasFiado,
        BigDecimal totalVendasValeTroca,
        String usuarioAbertura,
        String usuarioFechamento,
        String observacao,
        LocalDateTime abertoEm,
        LocalDateTime fechadoEm,
        List<MovimentoCaixaResponse> movimentos
) {

    public static CaixaSessaoResponse fromEntity(CaixaSessao caixaSessao) {
        return new CaixaSessaoResponse(
                caixaSessao.getId(),
                caixaSessao.getStatus().name(),
                caixaSessao.getFundoTrocoInicial(),
                caixaSessao.getValorInformadoFechamento(),
                caixaSessao.getValorEsperadoFechamento(),
                caixaSessao.getTotalSangrias(),
                caixaSessao.getTotalSuprimentos(),
                caixaSessao.getTotalVendasDinheiro(),
                caixaSessao.getTotalVendasPix(),
                caixaSessao.getTotalVendasCartao(),
                caixaSessao.getTotalVendasFiado(),
                caixaSessao.getTotalVendasValeTroca(),
                caixaSessao.getUsuarioAbertura().getNome(),
                caixaSessao.getUsuarioFechamento() == null ? null : caixaSessao.getUsuarioFechamento().getNome(),
                caixaSessao.getObservacao(),
                caixaSessao.getAbertoEm(),
                caixaSessao.getFechadoEm(),
                caixaSessao.getMovimentos() == null ? List.of() : caixaSessao.getMovimentos().stream()
                        .map(MovimentoCaixaResponse::fromEntity)
                        .toList()
        );
    }
}
