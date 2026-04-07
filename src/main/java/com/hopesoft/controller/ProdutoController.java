package com.hopesoft.controller;

import com.hopesoft.dto.ProdutoRequest;
import com.hopesoft.dto.ProdutoResponse;
import com.hopesoft.model.Categoria;
import com.hopesoft.model.Produto;
import com.hopesoft.security.HopesoftUserDetails;
import com.hopesoft.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> listar(
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        List<Produto> produtos = produtoService.listarProdutosPorEmpresa(userDetails.getEmpresaId());
        List<ProdutoResponse> responses = produtos.stream()
                .map(ProdutoResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        Produto produto = produtoService.buscarProdutoPorId(userDetails.getEmpresaId(), id);
        return ResponseEntity.ok(ProdutoResponse.fromEntity(produto));
    }

    @PostMapping
    public ResponseEntity<ProdutoResponse> criar(
            @Valid @RequestBody ProdutoRequest request,
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        Produto produto = new Produto();
        produto.setNome(request.nome());
        produto.setCodigoBarras(request.codigoBarras());
        produto.setPreco(request.preco());
        produto.setEstoque(request.estoque());
        produto.setEstoqueMin(request.estoqueMin());
        produto.setAtivo(request.ativo() != null ? request.ativo() : true);

        if (request.categoriaId() != null) {
            Categoria categoria = new Categoria();
            categoria.setId(request.categoriaId());
            produto.setCategoria(categoria);
        }

        Produto produtoCriado = produtoService.criarProduto(userDetails.getEmpresaId(), produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProdutoResponse.fromEntity(produtoCriado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProdutoRequest request,
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        Produto produto = new Produto();
        produto.setNome(request.nome());
        produto.setCodigoBarras(request.codigoBarras());
        produto.setPreco(request.preco());
        produto.setEstoque(request.estoque());
        produto.setEstoqueMin(request.estoqueMin());
        produto.setAtivo(request.ativo() != null ? request.ativo() : true);

        if (request.categoriaId() != null) {
            Categoria categoria = new Categoria();
            categoria.setId(request.categoriaId());
            produto.setCategoria(categoria);
        }

        Produto produtoAtualizado = produtoService.atualizarProduto(userDetails.getEmpresaId(), id, produto);
        return ResponseEntity.ok(ProdutoResponse.fromEntity(produtoAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(
            @PathVariable Long id,
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        produtoService.desativarProduto(userDetails.getEmpresaId(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/estoque/baixo")
    public ResponseEntity<List<ProdutoResponse>> listarEstoqueBaixo(
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        List<Produto> produtos = produtoService.listarProdutosComEstoqueBaixo(userDetails.getEmpresaId());
        List<ProdutoResponse> responses = produtos.stream()
                .map(ProdutoResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ProdutoResponse>> buscarPorNome(
            @RequestParam String nome,
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        List<Produto> produtos = produtoService.buscarProdutosPorNome(userDetails.getEmpresaId(), nome);
        List<ProdutoResponse> responses = produtos.stream()
                .map(ProdutoResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(responses);
    }
}

