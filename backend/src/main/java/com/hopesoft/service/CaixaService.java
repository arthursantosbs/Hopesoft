package com.hopesoft.service;

import com.hopesoft.dto.CaixaAberturaRequest;
import com.hopesoft.dto.CaixaFechamentoRequest;
import com.hopesoft.dto.CaixaMovimentoRequest;
import com.hopesoft.exception.CaixaOperacaoException;
import com.hopesoft.model.CaixaSessao;
import com.hopesoft.model.Empresa;
import com.hopesoft.model.FormaPagamento;
import com.hopesoft.model.MovimentoCaixa;
import com.hopesoft.model.PagamentoVenda;
import com.hopesoft.model.StatusCaixaSessao;
import com.hopesoft.model.TipoMovimentoCaixa;
import com.hopesoft.model.Usuario;
import com.hopesoft.model.Venda;
import com.hopesoft.repository.CaixaSessaoRepository;
import com.hopesoft.repository.MovimentoCaixaRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CaixaService {

    private final CaixaSessaoRepository caixaSessaoRepository;
    private final MovimentoCaixaRepository movimentoCaixaRepository;
    private final EmpresaService empresaService;
    private final UsuarioServiceHelper usuarioServiceHelper;

    @Transactional
    public CaixaSessao abrirSessao(Long empresaId, Long usuarioId, CaixaAberturaRequest request) {
        Empresa empresa = empresaService.buscarEmpresaPorId(empresaId);

        if (caixaSessaoRepository.findFirstByEmpresaAndStatusOrderByAbertoEmDesc(empresa, StatusCaixaSessao.ABERTO).isPresent()) {
            throw new CaixaOperacaoException("Ja existe um caixa aberto para a empresa");
        }

        Usuario usuario = usuarioServiceHelper.buscarUsuarioDaEmpresa(usuarioId, empresaId);

        CaixaSessao caixaSessao = CaixaSessao.builder()
                .empresa(empresa)
                .usuarioAbertura(usuario)
                .fundoTrocoInicial(request.fundoTrocoInicial())
                .valorEsperadoFechamento(request.fundoTrocoInicial())
                .observacao(request.observacao())
                .status(StatusCaixaSessao.ABERTO)
                .build();

        CaixaSessao salvo = caixaSessaoRepository.save(caixaSessao);
        registrarMovimento(salvo, usuario, TipoMovimentoCaixa.ABERTURA, request.fundoTrocoInicial(), request.observacao());
        return caixaSessaoRepository.save(salvo);
    }

    @Transactional(readOnly = true)
    public Optional<CaixaSessao> buscarSessaoAtual(Long empresaId) {
        Empresa empresa = empresaService.buscarEmpresaPorId(empresaId);
        return caixaSessaoRepository.findFirstByEmpresaAndStatusOrderByAbertoEmDesc(empresa, StatusCaixaSessao.ABERTO);
    }

    @Transactional(readOnly = true)
    public List<CaixaSessao> listarSessoes(Long empresaId) {
        Empresa empresa = empresaService.buscarEmpresaPorId(empresaId);
        return caixaSessaoRepository.findByEmpresaOrderByAbertoEmDesc(empresa);
    }

    @Transactional
    public CaixaSessao registrarSangria(Long empresaId, Long usuarioId, CaixaMovimentoRequest request) {
        CaixaSessao caixaSessao = buscarSessaoAtualObrigatoria(empresaId);
        Usuario usuario = usuarioServiceHelper.buscarUsuarioDaEmpresa(usuarioId, empresaId);
        caixaSessao.setTotalSangrias(caixaSessao.getTotalSangrias().add(request.valor()));
        caixaSessao.setValorEsperadoFechamento(calcularValorEsperado(caixaSessao));
        registrarMovimento(caixaSessao, usuario, TipoMovimentoCaixa.SANGRIA, request.valor(), request.observacao());
        return caixaSessaoRepository.save(caixaSessao);
    }

    @Transactional
    public CaixaSessao registrarSuprimento(Long empresaId, Long usuarioId, CaixaMovimentoRequest request) {
        CaixaSessao caixaSessao = buscarSessaoAtualObrigatoria(empresaId);
        Usuario usuario = usuarioServiceHelper.buscarUsuarioDaEmpresa(usuarioId, empresaId);
        caixaSessao.setTotalSuprimentos(caixaSessao.getTotalSuprimentos().add(request.valor()));
        caixaSessao.setValorEsperadoFechamento(calcularValorEsperado(caixaSessao));
        registrarMovimento(caixaSessao, usuario, TipoMovimentoCaixa.SUPRIMENTO, request.valor(), request.observacao());
        return caixaSessaoRepository.save(caixaSessao);
    }

    @Transactional
    public CaixaSessao fecharSessao(Long empresaId, Long usuarioId, CaixaFechamentoRequest request) {
        CaixaSessao caixaSessao = buscarSessaoAtualObrigatoria(empresaId);
        Usuario usuario = usuarioServiceHelper.buscarUsuarioDaEmpresa(usuarioId, empresaId);
        caixaSessao.setUsuarioFechamento(usuario);
        caixaSessao.setValorInformadoFechamento(request.valorInformadoFechamento());
        caixaSessao.setValorEsperadoFechamento(calcularValorEsperado(caixaSessao));
        caixaSessao.setObservacao(request.observacao());
        caixaSessao.setStatus(StatusCaixaSessao.FECHADO);
        caixaSessao.setFechadoEm(LocalDateTime.now());
        registrarMovimento(caixaSessao, usuario, TipoMovimentoCaixa.FECHAMENTO, request.valorInformadoFechamento(), request.observacao());
        return caixaSessaoRepository.save(caixaSessao);
    }

    @Transactional
    public void registrarVendaFinalizada(Long empresaId, Venda venda) {
        Optional<CaixaSessao> optionalSessao = buscarSessaoAtual(empresaId);
        if (optionalSessao.isEmpty()) {
            return;
        }

        CaixaSessao caixaSessao = optionalSessao.get();

        for (PagamentoVenda pagamento : venda.getPagamentos()) {
            BigDecimal valor = pagamento.getValor();
            switch (pagamento.getFormaPagamento()) {
                case DINHEIRO -> caixaSessao.setTotalVendasDinheiro(caixaSessao.getTotalVendasDinheiro().add(valor));
                case PIX -> caixaSessao.setTotalVendasPix(caixaSessao.getTotalVendasPix().add(valor));
                case CARTAO_CREDITO, CARTAO_DEBITO -> caixaSessao.setTotalVendasCartao(caixaSessao.getTotalVendasCartao().add(valor));
                case FIADO -> caixaSessao.setTotalVendasFiado(caixaSessao.getTotalVendasFiado().add(valor));
                case VALE_TROCA -> caixaSessao.setTotalVendasValeTroca(caixaSessao.getTotalVendasValeTroca().add(valor));
                default -> {
                }
            }
        }

        caixaSessao.setValorEsperadoFechamento(calcularValorEsperado(caixaSessao));
        caixaSessaoRepository.save(caixaSessao);
    }

    public CaixaSessao buscarSessaoAtualObrigatoria(Long empresaId) {
        return buscarSessaoAtual(empresaId)
                .orElseThrow(() -> new CaixaOperacaoException("Nao existe caixa aberto para a empresa"));
    }

    private void registrarMovimento(
            CaixaSessao caixaSessao,
            Usuario usuario,
            TipoMovimentoCaixa tipo,
            BigDecimal valor,
            String observacao
    ) {
        MovimentoCaixa movimentoCaixa = MovimentoCaixa.builder()
                .caixaSessao(caixaSessao)
                .usuario(usuario)
                .tipo(tipo)
                .valor(valor)
                .observacao(observacao)
                .build();
        movimentoCaixaRepository.save(movimentoCaixa);
    }

    private BigDecimal calcularValorEsperado(CaixaSessao caixaSessao) {
        return caixaSessao.getFundoTrocoInicial()
                .add(caixaSessao.getTotalSuprimentos())
                .add(caixaSessao.getTotalVendasDinheiro())
                .subtract(caixaSessao.getTotalSangrias());
    }
}
