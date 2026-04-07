package com.hopesoft.service;

import com.hopesoft.exception.EstoqueInsuficienteException;
import com.hopesoft.exception.ProdutoNotFoundException;
import com.hopesoft.model.FormaPagamento;
import com.hopesoft.model.ItemVenda;
import com.hopesoft.model.Produto;
import com.hopesoft.model.Venda;
import com.hopesoft.repository.ProdutoRepository;
import com.hopesoft.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;

    @Transactional
    public Venda registrarVenda(Venda venda) {
        venda.getItens().forEach(item -> {
            Produto produto = produtoRepository.findById(item.getProduto().getId())
                    .orElseThrow(() -> new ProdutoNotFoundException("Produto não encontrado"));

            if (produto.getEstoque() < item.getQuantidade()) {
                throw new EstoqueInsuficienteException("Estoque insuficiente para o produto: " + produto.getNome());
            }

            produto.setEstoque(produto.getEstoque() - item.getQuantidade());
            produtoRepository.save(produto);

            item.setPrecoUnit(produto.getPreco());
            item.setSubtotal(produto.getPreco().multiply(new BigDecimal(item.getQuantidade())));
            item.setVenda(venda);
        });

        venda.setTotal(calcularTotal(venda));

        return vendaRepository.save(venda);
    }

    public BigDecimal calcularTotal(Venda venda) {
        return venda.getItens().stream()
                .map(ItemVenda::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularTroco(Venda venda, BigDecimal valorPago) {
        if (venda.getFormaPagamento() == FormaPagamento.DINHEIRO) {
            return valorPago.subtract(venda.getTotal());
        }
        return BigDecimal.ZERO;
    }
}
