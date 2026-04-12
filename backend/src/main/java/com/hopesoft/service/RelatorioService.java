package com.hopesoft.service;

import com.hopesoft.dto.RelatorioFormaPagamentoResponse;
import com.hopesoft.model.Empresa;
import com.hopesoft.repository.PagamentoVendaRepository;
import com.hopesoft.repository.TotalPorFormaPagamentoProjection;
import com.hopesoft.repository.VendaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final VendaRepository vendaRepository;
    private final PagamentoVendaRepository pagamentoVendaRepository;
    private final EmpresaService empresaService;

    public BigDecimal totalDoDia(Long empresaId, LocalDate dia) {
        Empresa empresa = empresaService.buscarEmpresaPorId(empresaId);
        LocalDateTime inicio = dia.atStartOfDay();
        LocalDateTime fim = dia.atTime(LocalTime.MAX);
        return vendaRepository.sumTotalByEmpresaAndPeriodo(empresa, inicio, fim);
    }

    public Long numeroDeVendas(Long empresaId, LocalDate dia) {
        Empresa empresa = empresaService.buscarEmpresaPorId(empresaId);
        LocalDateTime inicio = dia.atStartOfDay();
        LocalDateTime fim = dia.atTime(LocalTime.MAX);
        return vendaRepository.countByEmpresaAndPeriodo(empresa, inicio, fim);
    }

    public List<RelatorioFormaPagamentoResponse> totalPorFormaDePagamento(Long empresaId, LocalDate dia) {
        Empresa empresa = empresaService.buscarEmpresaPorId(empresaId);
        LocalDateTime inicio = dia.atStartOfDay();
        LocalDateTime fim = dia.atTime(LocalTime.MAX);
        return pagamentoVendaRepository.sumTotalByFormaPagamento(empresa, inicio, fim)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private RelatorioFormaPagamentoResponse toResponse(TotalPorFormaPagamentoProjection projection) {
        return new RelatorioFormaPagamentoResponse(
                projection.getFormaPagamento().name(),
                projection.getTotal()
        );
    }
}
