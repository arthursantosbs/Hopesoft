package com.hopesoft.service;

import com.hopesoft.dto.ValeTrocaItemRequest;
import com.hopesoft.dto.ValeTrocaRequest;
import com.hopesoft.exception.InvalidPayloadException;
import com.hopesoft.exception.ValeTrocaNotFoundException;
import com.hopesoft.model.Empresa;
import com.hopesoft.model.ItemVenda;
import com.hopesoft.model.StatusValeTroca;
import com.hopesoft.model.ValeTroca;
import com.hopesoft.model.Venda;
import com.hopesoft.repository.ValeTrocaRepository;
import com.hopesoft.repository.VendaRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ValeTrocaService {

    private final ValeTrocaRepository valeTrocaRepository;
    private final VendaRepository vendaRepository;
    private final EmpresaService empresaService;

    @Transactional
    public ValeTroca gerarValeTroca(Long empresaId, Long vendaId, ValeTrocaRequest request) {
        Empresa empresa = empresaService.buscarEmpresaPorId(empresaId);
        Venda venda = vendaRepository.findByIdAndEmpresa(empresa, vendaId)
                .orElseThrow(() -> new InvalidPayloadException("Venda nao encontrada para gerar vale-troca"));

        BigDecimal credito = BigDecimal.ZERO;

        for (ValeTrocaItemRequest itemRequest : request.itens()) {
            ItemVenda itemVenda = venda.getItens().stream()
                    .filter(item -> item.getProduto().getId().equals(itemRequest.produtoId()))
                    .findFirst()
                    .orElseThrow(() -> new InvalidPayloadException("Item nao encontrado na venda para devolucao"));

            int disponivel = itemVenda.getQuantidade() - itemVenda.getQuantidadeDevolvida();
            if (itemRequest.quantidade() > disponivel) {
                throw new InvalidPayloadException("Quantidade devolvida maior que a disponivel para o item");
            }

            BigDecimal valorUnitarioLiquido = itemVenda.getSubtotal()
                    .divide(BigDecimal.valueOf(itemVenda.getQuantidade()), 2, RoundingMode.HALF_UP);
            credito = credito.add(valorUnitarioLiquido.multiply(BigDecimal.valueOf(itemRequest.quantidade())));
            itemVenda.setQuantidadeDevolvida(itemVenda.getQuantidadeDevolvida() + itemRequest.quantidade());
        }

        ValeTroca valeTroca = ValeTroca.builder()
                .empresa(empresa)
                .vendaOrigem(venda)
                .codigo("VT-" + System.currentTimeMillis())
                .nomeCliente(request.nomeCliente())
                .documentoCliente(request.documentoCliente())
                .saldoOriginal(credito)
                .saldo(credito)
                .status(StatusValeTroca.ATIVO)
                .observacao(request.observacao())
                .expiraEm(LocalDateTime.now().plusDays(90))
                .build();

        return valeTrocaRepository.save(valeTroca);
    }

    public ValeTroca buscarValeAtivo(Long empresaId, String codigo) {
        Empresa empresa = empresaService.buscarEmpresaPorId(empresaId);
        ValeTroca valeTroca = valeTrocaRepository.findByCodigoAndEmpresa(codigo, empresa)
                .orElseThrow(() -> new ValeTrocaNotFoundException("Vale-troca nao encontrado"));

        if (valeTroca.getExpiraEm() != null && valeTroca.getExpiraEm().isBefore(LocalDateTime.now())) {
            valeTroca.setStatus(StatusValeTroca.EXPIRADO);
            valeTrocaRepository.save(valeTroca);
            throw new InvalidPayloadException("Vale-troca expirado");
        }

        if (valeTroca.getStatus() != StatusValeTroca.ATIVO) {
            throw new InvalidPayloadException("Vale-troca nao esta disponivel para uso");
        }

        return valeTroca;
    }

    @Transactional
    public void consumirVale(Long empresaId, String codigo, BigDecimal valor) {
        ValeTroca valeTroca = buscarValeAtivo(empresaId, codigo);

        if (valeTroca.getSaldo().compareTo(valor) < 0) {
            throw new InvalidPayloadException("Saldo do vale-troca insuficiente");
        }

        valeTroca.setSaldo(valeTroca.getSaldo().subtract(valor));
        if (valeTroca.getSaldo().compareTo(BigDecimal.ZERO) == 0) {
            valeTroca.setStatus(StatusValeTroca.CONSUMIDO);
        }

        valeTrocaRepository.save(valeTroca);
    }

    public List<ValeTroca> listarValesAtivos(Long empresaId) {
        Empresa empresa = empresaService.buscarEmpresaPorId(empresaId);
        return valeTrocaRepository.findByEmpresaAndStatusOrderByCriadoEmDesc(empresa, StatusValeTroca.ATIVO);
    }
}
