package com.hopesoft.service;

import com.hopesoft.model.Empresa;
import com.hopesoft.model.FormaPagamento;
import com.hopesoft.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final VendaRepository vendaRepository;

    public BigDecimal totalDoDia(Empresa empresa, LocalDate dia) {
        LocalDateTime inicio = dia.atStartOfDay();
        LocalDateTime fim = dia.atTime(LocalTime.MAX);
        return vendaRepository.sumTotalByEmpresaAndPeriodo(empresa, inicio, fim);
    }

    public Long numeroDeVendas(Empresa empresa, LocalDate dia) {
        LocalDateTime inicio = dia.atStartOfDay();
        LocalDateTime fim = dia.atTime(LocalTime.MAX);
        return vendaRepository.countByEmpresaAndPeriodo(empresa, inicio, fim);
    }

    public Map<FormaPagamento, BigDecimal> totalPorFormaDePagamento(Empresa empresa, LocalDate dia) {
        LocalDateTime inicio = dia.atStartOfDay();
        LocalDateTime fim = dia.atTime(LocalTime.MAX);
        return vendaRepository.sumTotalByFormaPagamento(empresa, inicio, fim);
    }
}
