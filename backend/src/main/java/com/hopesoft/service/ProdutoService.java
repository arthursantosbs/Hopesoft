package com.hopesoft.service;

import com.hopesoft.exception.ProdutoNotFoundException;
import com.hopesoft.model.Categoria;
import com.hopesoft.model.Empresa;
import com.hopesoft.model.Produto;
import com.hopesoft.repository.ProdutoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final EmpresaService empresaService;
    private final CategoriaService categoriaService;

    @Transactional
    public Produto criarProduto(Long empresaId, Produto produto) {
        Empresa empresa = empresaService.buscarEmpresaPorId(empresaId);
        produto.setEmpresa(empresa);
        produto.setAtivo(produto.getAtivo() == null ? true : produto.getAtivo());

        if (produto.getCategoria() != null && produto.getCategoria().getId() != null) {
            Categoria categoria = categoriaService.buscarCategoriaPorId(empresaId, produto.getCategoria().getId());
            produto.setCategoria(categoria);
        }

        return produtoRepository.save(produto);
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

    @Transactional
    public Produto atualizarProduto(Long empresaId, Long produtoId, Produto produtoAtualizado) {
        Produto produtoExistente = buscarProdutoPorId(empresaId, produtoId);

        produtoExistente.setNome(produtoAtualizado.getNome());
        produtoExistente.setCodigoBarras(produtoAtualizado.getCodigoBarras());
        produtoExistente.setPreco(produtoAtualizado.getPreco());
        produtoExistente.setEstoque(produtoAtualizado.getEstoque());
        produtoExistente.setEstoqueMin(produtoAtualizado.getEstoqueMin());
        produtoExistente.setAtivo(produtoAtualizado.getAtivo());

        if (produtoAtualizado.getCategoria() != null && produtoAtualizado.getCategoria().getId() != null) {
            Categoria categoria = categoriaService.buscarCategoriaPorId(empresaId, produtoAtualizado.getCategoria().getId());
            produtoExistente.setCategoria(categoria);
        } else {
            produtoExistente.setCategoria(null);
        }

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
}
