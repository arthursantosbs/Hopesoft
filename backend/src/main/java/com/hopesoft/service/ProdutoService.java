package com.hopesoft.service;

import com.hopesoft.dto.ProdutoGradeRequest;
import com.hopesoft.dto.ProdutoGradeResponse;
import com.hopesoft.dto.ProdutoGradeVarianteRequest;
import com.hopesoft.dto.ProdutoResponse;
import com.hopesoft.exception.InvalidPayloadException;
import com.hopesoft.exception.ProdutoNotFoundException;
import com.hopesoft.model.Categoria;
import com.hopesoft.model.Empresa;
import com.hopesoft.model.Produto;
import com.hopesoft.repository.ProdutoRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final EmpresaService empresaService;
    private final CategoriaService categoriaService;
    private final CodigoBarrasService codigoBarrasService;

    @Transactional
    public Produto criarProduto(Long empresaId, Produto produto) {
        Empresa empresa = empresaService.buscarEmpresaPorId(empresaId);
        prepararProduto(empresaId, produto);
        produto.setEmpresa(empresa);

        if (produto.getGradeGrupo() == null || produto.getGradeGrupo().isBlank()) {
            produto.setGradeGrupo(produto.getReferencia());
        }

        return produtoRepository.save(produto);
    }

    @Transactional
    public ProdutoGradeResponse criarGrade(Long empresaId, ProdutoGradeRequest request) {
        Empresa empresa = empresaService.buscarEmpresaPorId(empresaId);
        Categoria categoria = request.categoriaId() == null
                ? null
                : categoriaService.buscarCategoriaPorId(empresaId, request.categoriaId());

        String gradeGrupo = request.referencia() + "-" + LocalDateTime.now().toLocalDate();
        Map<String, ProdutoGradeVarianteRequest> overrides = new LinkedHashMap<>();

        if (request.variantes() != null) {
            for (ProdutoGradeVarianteRequest variante : request.variantes()) {
                overrides.put(chaveVariante(variante.cor(), variante.tamanho()), variante);
            }
        }

        List<Produto> variantes = new ArrayList<>();

        for (String cor : request.cores()) {
            for (String tamanho : request.tamanhos()) {
                ProdutoGradeVarianteRequest override = overrides.get(chaveVariante(cor, tamanho));
                Produto produto = Produto.builder()
                        .empresa(empresa)
                        .nome(request.nomeBase() + " " + cor + " " + tamanho)
                        .modelo(request.modelo())
                        .referencia(request.referencia())
                        .gradeGrupo(gradeGrupo)
                        .cor(cor)
                        .tamanho(tamanho)
                        .codigoBarras(gerarCodigoBarrasUnico(override == null ? null : override.codigoBarras(), request.referencia()))
                        .preco(override != null && override.preco() != null ? override.preco() : request.precoBase())
                        .estoque(override != null && override.estoque() != null ? override.estoque() : request.estoqueInicialPadrao())
                        .estoqueMin(request.estoqueMin())
                        .categoria(categoria)
                        .ativo(true)
                        .build();
                variantes.add(produtoRepository.save(produto));
            }
        }

        return new ProdutoGradeResponse(
                gradeGrupo,
                request.modelo(),
                request.referencia(),
                variantes.stream().map(ProdutoResponse::fromEntity).toList()
        );
    }

    public List<Produto> listarProdutosPorEmpresa(Long empresaId) {
        Empresa empresa = empresaService.buscarEmpresaPorId(empresaId);
        return produtoRepository.findByEmpresaAndAtivoTrueOrderByNomeAsc(empresa);
    }

    public Produto buscarProdutoPorId(Long empresaId, Long produtoId) {
        Empresa empresa = empresaService.buscarEmpresaPorId(empresaId);
        return produtoRepository.findByIdAndEmpresa(produtoId, empresa)
                .orElseThrow(() -> new ProdutoNotFoundException("Produto nao encontrado para a empresa informada"));
    }

    public Produto buscarProdutoAtivoPorId(Long empresaId, Long produtoId) {
        Empresa empresa = empresaService.buscarEmpresaPorId(empresaId);
        return produtoRepository.findByIdAndEmpresaAndAtivoTrue(produtoId, empresa)
                .orElseThrow(() -> new ProdutoNotFoundException("Produto ativo nao encontrado para a empresa informada"));
    }

    public Produto buscarPorCodigoBarras(Long empresaId, String codigoBarras) {
        Empresa empresa = empresaService.buscarEmpresaPorId(empresaId);
        return produtoRepository.findByCodigoBarrasAndEmpresa(codigoBarras, empresa)
                .orElseThrow(() -> new ProdutoNotFoundException("Produto nao encontrado para o codigo de barras informado"));
    }

    @Transactional
    public Produto atualizarProduto(Long empresaId, Long produtoId, Produto produtoAtualizado) {
        Produto produtoExistente = buscarProdutoPorId(empresaId, produtoId);
        prepararProduto(empresaId, produtoAtualizado);

        produtoExistente.setNome(produtoAtualizado.getNome());
        produtoExistente.setModelo(produtoAtualizado.getModelo());
        produtoExistente.setReferencia(produtoAtualizado.getReferencia());
        produtoExistente.setGradeGrupo(produtoAtualizado.getGradeGrupo() == null || produtoAtualizado.getGradeGrupo().isBlank()
                ? produtoExistente.getGradeGrupo()
                : produtoAtualizado.getGradeGrupo());
        produtoExistente.setCor(produtoAtualizado.getCor());
        produtoExistente.setTamanho(produtoAtualizado.getTamanho());
        produtoExistente.setCodigoBarras(produtoAtualizado.getCodigoBarras());
        produtoExistente.setPreco(produtoAtualizado.getPreco());
        produtoExistente.setEstoque(produtoAtualizado.getEstoque());
        produtoExistente.setEstoqueMin(produtoAtualizado.getEstoqueMin());
        produtoExistente.setAtivo(produtoAtualizado.getAtivo());
        produtoExistente.setCategoria(produtoAtualizado.getCategoria());

        return produtoRepository.save(produtoExistente);
    }

    @Transactional
    public void desativarProduto(Long empresaId, Long produtoId) {
        Produto produto = buscarProdutoPorId(empresaId, produtoId);
        produto.setAtivo(false);
        produtoRepository.save(produto);
    }

    public List<Produto> buscarProdutosPorNome(Long empresaId, String nome) {
        Empresa empresa = empresaService.buscarEmpresaPorId(empresaId);
        if (nome == null || nome.isBlank()) {
            return produtoRepository.findByEmpresaAndAtivoTrueOrderByNomeAsc(empresa);
        }
        return produtoRepository.findByEmpresaAndAtivoTrueAndNomeContainingIgnoreCaseOrderByNomeAsc(empresa, nome);
    }

    public List<Produto> listarProdutosComEstoqueBaixo(Long empresaId) {
        Empresa empresa = empresaService.buscarEmpresaPorId(empresaId);
        return produtoRepository.findEstoqueBaixo(empresa);
    }

    public List<Produto> listarVariantesPorGrade(Long empresaId, String gradeGrupo) {
        Empresa empresa = empresaService.buscarEmpresaPorId(empresaId);
        return produtoRepository.findByGradeGrupoAndEmpresaOrderByNomeAsc(gradeGrupo, empresa);
    }

    private void prepararProduto(Long empresaId, Produto produto) {
        if (produto.getModelo() == null || produto.getModelo().isBlank()) {
            produto.setModelo(produto.getNome());
        }

        if (produto.getReferencia() == null || produto.getReferencia().isBlank()) {
            produto.setReferencia(produto.getModelo().replaceAll("\\s+", "-").toUpperCase());
        }

        if (produto.getCodigoBarras() == null || produto.getCodigoBarras().isBlank()) {
            produto.setCodigoBarras(gerarCodigoBarrasUnico(null, produto.getReferencia()));
        } else {
            validarCodigoBarrasUnico(produto.getCodigoBarras(), produto.getId());
        }

        produto.setAtivo(produto.getAtivo() == null ? true : produto.getAtivo());

        if (produto.getCategoria() != null && produto.getCategoria().getId() != null) {
            Categoria categoria = categoriaService.buscarCategoriaPorId(empresaId, produto.getCategoria().getId());
            produto.setCategoria(categoria);
        } else {
            produto.setCategoria(null);
        }
    }

    private String chaveVariante(String cor, String tamanho) {
        return (cor == null ? "" : cor.trim().toUpperCase()) + "::" + (tamanho == null ? "" : tamanho.trim().toUpperCase());
    }

    private String gerarCodigoBarrasUnico(String informado, String referencia) {
        if (informado != null && !informado.isBlank()) {
            validarCodigoBarrasUnico(informado, null);
            return informado;
        }

        String candidato;

        do {
            candidato = codigoBarrasService.gerarEan13(referencia);
        } while (produtoRepository.existsByCodigoBarras(candidato));

        return candidato;
    }

    private void validarCodigoBarrasUnico(String codigoBarras, Long produtoAtualId) {
        Optional<Produto> existente = produtoRepository.findAll().stream()
                .filter(produto -> codigoBarras.equals(produto.getCodigoBarras()))
                .findFirst();

        if (existente.isPresent() && (produtoAtualId == null || !existente.get().getId().equals(produtoAtualId))) {
            throw new InvalidPayloadException("Codigo de barras ja utilizado por outro produto");
        }
    }
}
