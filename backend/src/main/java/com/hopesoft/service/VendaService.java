package com.hopesoft.service;

import com.hopesoft.dto.ItemVendaRequest;
import com.hopesoft.dto.VendaRequest;
import com.hopesoft.exception.EstoqueInsuficienteException;
import com.hopesoft.exception.InvalidPayloadException;
import com.hopesoft.exception.ProdutoNotFoundException;
import com.hopesoft.exception.UsuarioNotFoundException;
import com.hopesoft.model.Empresa;
import com.hopesoft.model.FormaPagamento;
import com.hopesoft.model.ItemVenda;
import com.hopesoft.model.Produto;
import com.hopesoft.model.Usuario;
import com.hopesoft.model.Venda;
import com.hopesoft.repository.ProdutoRepository;
import com.hopesoft.repository.UsuarioRepository;
import com.hopesoft.repository.VendaRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaService empresaService;

    @Transactional
    public Venda registrarVenda(Long empresaId, Long usuarioId, VendaRequest request) {
        Empresa empresa = empresaService.buscarEmpresaPorId(empresaId);
        Usuario usuario = usuarioRepository.findByIdAndEmpresa_Id(usuarioId, empresaId)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario nao encontrado para a empresa informada"));

        Venda venda = Venda.builder()
                .empresa(empresa)
                .usuario(usuario)
                .formaPagamento(request.formaPagamento())
                .build();

        List<ItemVenda> itens = request.itens().stream()
                .map(itemRequest -> criarItemVenda(empresa, venda, itemRequest))
                .toList();

        venda.setItens(itens);
        venda.setTotal(calcularTotal(venda));
        venda.setTroco(calcularTroco(venda, request.valorRecebido()));
        return vendaRepository.save(venda);
    }

    public BigDecimal calcularTotal(Venda venda) {
        return venda.getItens().stream()
                .map(ItemVenda::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularTroco(Venda venda, BigDecimal valorPago) {
        if (venda.getFormaPagamento() != FormaPagamento.DINHEIRO) {
            return BigDecimal.ZERO;
        }
        if (valorPago == null) {
            throw new InvalidPayloadException("Valor recebido e obrigatorio para pagamento em dinheiro");
        }
        if (valorPago.compareTo(venda.getTotal()) < 0) {
            throw new InvalidPayloadException("Valor recebido menor que o total da venda");
        }
        return valorPago.subtract(venda.getTotal());
    }

    private ItemVenda criarItemVenda(Empresa empresa, Venda venda, ItemVendaRequest itemRequest) {
        Produto produto = produtoRepository.findByIdAndEmpresaAndAtivoTrue(itemRequest.produtoId(), empresa)
                .orElseThrow(() -> new ProdutoNotFoundException("Produto ativo nao encontrado para a empresa informada"));

        if (produto.getEstoque() < itemRequest.quantidade()) {
            throw new EstoqueInsuficienteException("Estoque insuficiente para o produto: " + produto.getNome());
        }

        produto.setEstoque(produto.getEstoque() - itemRequest.quantidade());

        BigDecimal precoUnitario = produto.getPreco();
        BigDecimal subtotal = precoUnitario.multiply(BigDecimal.valueOf(itemRequest.quantidade()));

        return ItemVenda.builder()
                .venda(venda)
                .produto(produto)
                .quantidade(itemRequest.quantidade())
                .precoUnit(precoUnitario)
                .subtotal(subtotal)
                .build();
    }
}
