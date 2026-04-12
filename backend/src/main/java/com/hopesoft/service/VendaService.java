package com.hopesoft.service;

import com.hopesoft.dto.ItemVendaRequest;
import com.hopesoft.dto.PagamentoVendaRequest;
import com.hopesoft.dto.VendaRequest;
import com.hopesoft.exception.EstoqueInsuficienteException;
import com.hopesoft.exception.InvalidPayloadException;
import com.hopesoft.exception.ProdutoNotFoundException;
import com.hopesoft.model.Empresa;
import com.hopesoft.model.FormaPagamento;
import com.hopesoft.model.ItemVenda;
import com.hopesoft.model.PagamentoVenda;
import com.hopesoft.model.Perfil;
import com.hopesoft.model.Produto;
import com.hopesoft.model.StatusVenda;
import com.hopesoft.model.Usuario;
import com.hopesoft.model.Venda;
import com.hopesoft.repository.ProdutoRepository;
import com.hopesoft.repository.VendaRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VendaService {

    private static final BigDecimal LIMITE_DESCONTO_OPERADOR_PERCENTUAL = new BigDecimal("10");

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;
    private final EmpresaService empresaService;
    private final UsuarioServiceHelper usuarioServiceHelper;
    private final PasswordEncoder passwordEncoder;
    private final CaixaService caixaService;
    private final ValeTrocaService valeTrocaService;

    @Transactional
    public Venda registrarVenda(Long empresaId, Long usuarioId, VendaRequest request) {
        Empresa empresa = empresaService.buscarEmpresaPorId(empresaId);
        Usuario usuario = usuarioServiceHelper.buscarUsuarioDaEmpresa(usuarioId, empresaId);
        Venda venda = construirVenda(empresa, usuario, request);

        if (venda.getStatus() == StatusVenda.FINALIZADA) {
            aplicarBaixaDeEstoque(venda);
            caixaService.registrarVendaFinalizada(empresaId, venda);
        }

        return vendaRepository.save(venda);
    }

    @Transactional
    public Venda finalizarVendaEmEspera(Long empresaId, Long usuarioId, Long vendaId, VendaRequest request) {
        Empresa empresa = empresaService.buscarEmpresaPorId(empresaId);
        Usuario usuario = usuarioServiceHelper.buscarUsuarioDaEmpresa(usuarioId, empresaId);
        Venda vendaExistente = vendaRepository.findByIdAndEmpresa(empresa, vendaId)
                .orElseThrow(() -> new InvalidPayloadException("Venda em espera nao encontrada"));

        if (vendaExistente.getStatus() != StatusVenda.EM_ESPERA) {
            throw new InvalidPayloadException("Somente vendas em espera podem ser finalizadas por este fluxo");
        }

        vendaExistente.setItens(new ArrayList<>());
        vendaExistente.setPagamentos(new ArrayList<>());

        Venda vendaFinalizada = construirVenda(empresa, usuario, request);
        vendaExistente.setUsuario(usuario);
        vendaExistente.setItens(vendaFinalizada.getItens());
        vendaExistente.setPagamentos(vendaFinalizada.getPagamentos());
        vendaExistente.setSubtotalBruto(vendaFinalizada.getSubtotalBruto());
        vendaExistente.setDescontoTotal(vendaFinalizada.getDescontoTotal());
        vendaExistente.setAcrescimoTotal(vendaFinalizada.getAcrescimoTotal());
        vendaExistente.setTotal(vendaFinalizada.getTotal());
        vendaExistente.setFormaPagamento(vendaFinalizada.getFormaPagamento());
        vendaExistente.setTroco(vendaFinalizada.getTroco());
        vendaExistente.setObservacao(vendaFinalizada.getObservacao());
        vendaExistente.setStatus(vendaFinalizada.getStatus());

        for (ItemVenda item : vendaExistente.getItens()) {
            item.setVenda(vendaExistente);
        }
        for (PagamentoVenda pagamento : vendaExistente.getPagamentos()) {
            pagamento.setVenda(vendaExistente);
        }

        aplicarBaixaDeEstoque(vendaExistente);
        Venda salva = vendaRepository.save(vendaExistente);
        caixaService.registrarVendaFinalizada(empresaId, salva);
        return salva;
    }

    @Transactional
    public Venda cancelarVendaEmEspera(Long empresaId, Long vendaId) {
        Empresa empresa = empresaService.buscarEmpresaPorId(empresaId);
        Venda venda = vendaRepository.findByIdAndEmpresa(empresa, vendaId)
                .orElseThrow(() -> new InvalidPayloadException("Venda em espera nao encontrada"));

        if (venda.getStatus() != StatusVenda.EM_ESPERA) {
            throw new InvalidPayloadException("Somente vendas em espera podem ser canceladas");
        }

        venda.setStatus(StatusVenda.CANCELADA);
        venda.setObservacao("Venda cancelada pelo operador");
        return vendaRepository.save(venda);
    }

    @Transactional(readOnly = true)
    public List<Venda> listarVendasDoDia(Long empresaId, LocalDate data) {
        Empresa empresa = empresaService.buscarEmpresaPorId(empresaId);
        LocalDate dia = data != null ? data : LocalDate.now();
        LocalDateTime inicio = dia.atStartOfDay();
        LocalDateTime fim = dia.atTime(LocalTime.MAX);
        return vendaRepository.findByEmpresaAndCriadoEmBetweenOrderByCriadoEmDesc(empresa, inicio, fim);
    }

    @Transactional(readOnly = true)
    public List<Venda> listarVendasEmEspera(Long empresaId) {
        Empresa empresa = empresaService.buscarEmpresaPorId(empresaId);
        return vendaRepository.findByEmpresaAndStatusOrderByCriadoEmDesc(empresa, StatusVenda.EM_ESPERA);
    }

    @Transactional(readOnly = true)
    public Venda buscarVendaPorId(Long empresaId, Long vendaId) {
        Empresa empresa = empresaService.buscarEmpresaPorId(empresaId);
        return vendaRepository.findByIdAndEmpresa(empresa, vendaId)
                .orElseThrow(() -> new InvalidPayloadException("Venda nao encontrada para a empresa informada"));
    }

    public BigDecimal calcularTotal(Venda venda) {
        return venda.getItens().stream()
                .map(ItemVenda::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .subtract(venda.getDescontoTotal())
                .add(venda.getAcrescimoTotal());
    }

    private Venda construirVenda(Empresa empresa, Usuario usuario, VendaRequest request) {
        Venda venda = Venda.builder()
                .empresa(empresa)
                .usuario(usuario)
                .observacao(request.observacao())
                .status(Boolean.TRUE.equals(request.vendaEmEspera()) ? StatusVenda.EM_ESPERA : StatusVenda.FINALIZADA)
                .build();

        List<ItemVenda> itens = request.itens().stream()
                .map(itemRequest -> criarItemVenda(empresa, venda, itemRequest))
                .toList();

        venda.setItens(itens);
        venda.setSubtotalBruto(calcularSubtotalBruto(venda));
        BigDecimal descontoItens = itens.stream()
                .map(ItemVenda::getDescontoValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal descontoVenda = normalizar(request.descontoValor());
        BigDecimal acrescimoVenda = normalizar(request.acrescimoValor());

        venda.setDescontoTotal(descontoItens.add(descontoVenda));
        venda.setAcrescimoTotal(acrescimoVenda);
        validarPoliticaDesconto(usuario, venda.getSubtotalBruto(), venda.getDescontoTotal(), request.senhaGerenteAutorizacao(), empresa.getId());

        BigDecimal total = calcularTotal(venda);
        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPayloadException("Total da venda deve ser maior que zero");
        }

        venda.setTotal(total);

        if (venda.getStatus() == StatusVenda.EM_ESPERA) {
            venda.setFormaPagamento(null);
            venda.setPagamentos(List.of());
            venda.setTroco(BigDecimal.ZERO);
            return venda;
        }

        List<PagamentoVenda> pagamentos = construirPagamentos(venda, request);
        venda.setPagamentos(pagamentos);
        venda.setFormaPagamento(pagamentos.size() > 1 ? FormaPagamento.MISTO : pagamentos.getFirst().getFormaPagamento());
        venda.setTroco(calcularTroco(pagamentos));

        return venda;
    }

    private BigDecimal calcularSubtotalBruto(Venda venda) {
        return venda.getItens().stream()
                .map(ItemVenda::getSubtotalBruto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<PagamentoVenda> construirPagamentos(Venda venda, VendaRequest request) {
        List<PagamentoVendaRequest> pagamentosRequest = request.pagamentos();

        if (pagamentosRequest == null || pagamentosRequest.isEmpty()) {
            pagamentosRequest = construirPagamentoLegado(request, venda.getTotal());
        }

        if (pagamentosRequest == null || pagamentosRequest.isEmpty()) {
            throw new InvalidPayloadException("Informe ao menos um pagamento para finalizar a venda");
        }

        List<PagamentoVenda> pagamentos = new ArrayList<>();
        BigDecimal totalPagamentos = BigDecimal.ZERO;

        for (PagamentoVendaRequest pagamentoRequest : pagamentosRequest) {
            if (pagamentoRequest.formaPagamento() == FormaPagamento.MISTO) {
                throw new InvalidPayloadException("Forma de pagamento MISTO e calculada automaticamente");
            }

            if (pagamentoRequest.formaPagamento() == FormaPagamento.VALE_TROCA) {
                if (pagamentoRequest.codigoValeTroca() == null || pagamentoRequest.codigoValeTroca().isBlank()) {
                    throw new InvalidPayloadException("Codigo do vale-troca e obrigatorio para este pagamento");
                }
                valeTrocaService.consumirVale(venda.getEmpresa().getId(), pagamentoRequest.codigoValeTroca(), pagamentoRequest.valor());
            }

            BigDecimal valorRecebido = pagamentoRequest.valorRecebido();
            if (pagamentoRequest.formaPagamento() == FormaPagamento.DINHEIRO) {
                valorRecebido = valorRecebido == null ? pagamentoRequest.valor() : valorRecebido;
                if (valorRecebido.compareTo(pagamentoRequest.valor()) < 0) {
                    throw new InvalidPayloadException("Valor recebido em dinheiro nao pode ser menor que o valor aplicado");
                }
            } else if (valorRecebido != null) {
                throw new InvalidPayloadException("Valor recebido so pode ser informado para pagamento em dinheiro");
            }

            PagamentoVenda pagamentoVenda = PagamentoVenda.builder()
                    .venda(venda)
                    .formaPagamento(pagamentoRequest.formaPagamento())
                    .valor(pagamentoRequest.valor())
                    .valorRecebido(valorRecebido)
                    .parcelas(pagamentoRequest.parcelas())
                    .codigoValeTroca(pagamentoRequest.codigoValeTroca())
                    .build();
            pagamentos.add(pagamentoVenda);
            totalPagamentos = totalPagamentos.add(pagamentoRequest.valor());
        }

        if (totalPagamentos.compareTo(venda.getTotal()) != 0) {
            throw new InvalidPayloadException("A soma dos pagamentos deve ser exatamente igual ao total da venda");
        }

        return pagamentos;
    }

    private List<PagamentoVendaRequest> construirPagamentoLegado(VendaRequest request, BigDecimal total) {
        if (request.formaPagamento() == null) {
            return List.of();
        }

        return List.of(new PagamentoVendaRequest(
                request.formaPagamento(),
                total,
                request.formaPagamento() == FormaPagamento.DINHEIRO ? request.valorRecebido() : null,
                null,
                null
        ));
    }

    private BigDecimal calcularTroco(List<PagamentoVenda> pagamentos) {
        return pagamentos.stream()
                .filter(pagamento -> pagamento.getFormaPagamento() == FormaPagamento.DINHEIRO)
                .map(pagamento -> pagamento.getValorRecebido().subtract(pagamento.getValor()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private ItemVenda criarItemVenda(Empresa empresa, Venda venda, ItemVendaRequest itemRequest) {
        Produto produto = produtoRepository.findByIdAndEmpresaAndAtivoTrue(itemRequest.produtoId(), empresa)
                .orElseThrow(() -> new ProdutoNotFoundException("Produto ativo nao encontrado para a empresa informada"));

        BigDecimal precoUnitario = produto.getPreco();
        BigDecimal subtotalBruto = precoUnitario.multiply(BigDecimal.valueOf(itemRequest.quantidade()));
        BigDecimal descontoValor = normalizar(itemRequest.descontoValor());

        if (descontoValor.compareTo(subtotalBruto) > 0) {
            throw new InvalidPayloadException("Desconto do item nao pode ser maior que o subtotal bruto");
        }

        return ItemVenda.builder()
                .venda(venda)
                .produto(produto)
                .quantidade(itemRequest.quantidade())
                .precoUnit(precoUnitario)
                .subtotalBruto(subtotalBruto)
                .descontoValor(descontoValor)
                .acrescimoValor(BigDecimal.ZERO)
                .subtotal(subtotalBruto.subtract(descontoValor))
                .build();
    }

    private void aplicarBaixaDeEstoque(Venda venda) {
        for (ItemVenda item : venda.getItens()) {
            Produto produto = item.getProduto();
            if (produto.getEstoque() < item.getQuantidade()) {
                throw new EstoqueInsuficienteException("Estoque insuficiente para o produto: " + produto.getNome());
            }
            produto.setEstoque(produto.getEstoque() - item.getQuantidade());
        }
    }

    private void validarPoliticaDesconto(
            Usuario usuario,
            BigDecimal subtotalBruto,
            BigDecimal descontoTotal,
            String senhaGerenteAutorizacao,
            Long empresaId
    ) {
        if (subtotalBruto.compareTo(BigDecimal.ZERO) == 0 || descontoTotal.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        BigDecimal percentual = descontoTotal
                .multiply(new BigDecimal("100"))
                .divide(subtotalBruto, 2, RoundingMode.HALF_UP);

        if (usuario.getPerfil() == Perfil.ADMIN || percentual.compareTo(LIMITE_DESCONTO_OPERADOR_PERCENTUAL) <= 0) {
            return;
        }

        if (senhaGerenteAutorizacao == null || senhaGerenteAutorizacao.isBlank()) {
            throw new InvalidPayloadException("Desconto acima do limite do operador exige senha do gerente");
        }

        boolean autorizou = usuarioServiceHelper.listarAdminsDaEmpresa(empresaId).stream()
                .anyMatch(admin -> passwordEncoder.matches(senhaGerenteAutorizacao, admin.getSenha()));

        if (!autorizou) {
            throw new InvalidPayloadException("Senha do gerente invalida para autorizar o desconto");
        }
    }

    private BigDecimal normalizar(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }
}
