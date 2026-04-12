package com.hopesoft.controller;

import com.hopesoft.dto.EtiquetaResponse;
import com.hopesoft.dto.ProdutoGradeRequest;
import com.hopesoft.dto.ProdutoGradeResponse;
import com.hopesoft.dto.ProdutoRequest;
import com.hopesoft.dto.ProdutoResponse;
import com.hopesoft.model.Categoria;
import com.hopesoft.model.Produto;
import com.hopesoft.security.HopesoftUserDetails;
import com.hopesoft.service.EtiquetaService;
import com.hopesoft.service.ProdutoService;
import jakarta.validation.Valid;
import java.util.List;
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

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;
    private final EtiquetaService etiquetaService;

    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> listar(
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        List<Produto> produtos = produtoService.listarProdutosPorEmpresa(userDetails.getEmpresaId());
        return ResponseEntity.ok(produtos.stream().map(ProdutoResponse::fromEntity).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        Produto produto = produtoService.buscarProdutoPorId(userDetails.getEmpresaId(), id);
        return ResponseEntity.ok(ProdutoResponse.fromEntity(produto));
    }

    @GetMapping("/codigo-barras/{codigoBarras}")
    public ResponseEntity<ProdutoResponse> buscarPorCodigoBarras(
            @PathVariable String codigoBarras,
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        Produto produto = produtoService.buscarPorCodigoBarras(userDetails.getEmpresaId(), codigoBarras);
        return ResponseEntity.ok(ProdutoResponse.fromEntity(produto));
    }

    @GetMapping("/grade/{gradeGrupo}")
    public ResponseEntity<List<ProdutoResponse>> listarVariantesDaGrade(
            @PathVariable String gradeGrupo,
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        List<Produto> produtos = produtoService.listarVariantesPorGrade(userDetails.getEmpresaId(), gradeGrupo);
        return ResponseEntity.ok(produtos.stream().map(ProdutoResponse::fromEntity).toList());
    }

    @PostMapping
    public ResponseEntity<ProdutoResponse> criar(
            @Valid @RequestBody ProdutoRequest request,
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        Produto produto = toEntity(request);
        Produto produtoCriado = produtoService.criarProduto(userDetails.getEmpresaId(), produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProdutoResponse.fromEntity(produtoCriado));
    }

    @PostMapping("/grade")
    public ResponseEntity<ProdutoGradeResponse> criarGrade(
            @Valid @RequestBody ProdutoGradeRequest request,
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        ProdutoGradeResponse response = produtoService.criarGrade(userDetails.getEmpresaId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProdutoRequest request,
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        Produto produtoAtualizado = produtoService.atualizarProduto(
                userDetails.getEmpresaId(),
                id,
                toEntity(request)
        );
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
        return ResponseEntity.ok(produtos.stream().map(ProdutoResponse::fromEntity).toList());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ProdutoResponse>> buscarPorNome(
            @RequestParam String nome,
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        List<Produto> produtos = produtoService.buscarProdutosPorNome(userDetails.getEmpresaId(), nome);
        return ResponseEntity.ok(produtos.stream().map(ProdutoResponse::fromEntity).toList());
    }

    @GetMapping("/{id}/etiqueta")
    public ResponseEntity<EtiquetaResponse> gerarEtiqueta(
            @PathVariable Long id,
            @RequestParam(defaultValue = "ZPL") String linguagem,
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        Produto produto = produtoService.buscarProdutoPorId(userDetails.getEmpresaId(), id);
        String conteudo = "EPL".equalsIgnoreCase(linguagem)
                ? etiquetaService.gerarEpl(produto)
                : etiquetaService.gerarZpl(produto);
        return ResponseEntity.ok(new EtiquetaResponse(linguagem.toUpperCase(), conteudo));
    }

    private Produto toEntity(ProdutoRequest request) {
        Produto produto = new Produto();
        produto.setNome(request.nome());
        produto.setModelo(request.modelo());
        produto.setReferencia(request.referencia());
        produto.setGradeGrupo(request.referencia());
        produto.setCor(request.cor());
        produto.setTamanho(request.tamanho());
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

        return produto;
    }
}
