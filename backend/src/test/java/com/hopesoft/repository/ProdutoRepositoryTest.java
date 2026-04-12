package com.hopesoft.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.hopesoft.model.Categoria;
import com.hopesoft.model.Empresa;
import com.hopesoft.model.Produto;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

@DataJpaTest
class ProdutoRepositoryTest {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByEmpresaAndAtivoTrueOrderByNomeAscShouldReturnOnlyActiveProductsFromEmpresa() {
        Empresa empresaA = persistEmpresa("Mercado A", "77777777000177");
        Empresa empresaB = persistEmpresa("Mercado B", "88888888000188");

        Categoria categoriaA = persistCategoria(empresaA, "Mercearia");
        Categoria categoriaB = persistCategoria(empresaB, "Mercearia");

        Produto arroz = persistProduto(empresaA, categoriaA, "Arroz 5kg", true, 3, 5);
        Produto feijao = persistProduto(empresaA, categoriaA, "Feijao Preto", true, 10, 3);
        persistProduto(empresaA, categoriaA, "Arroz Integral", false, 8, 3);
        persistProduto(empresaB, categoriaB, "Arroz 1kg", true, 2, 4);
        entityManager.clear();

        List<Produto> produtosAtivos = produtoRepository.findByEmpresaAndAtivoTrueOrderByNomeAsc(empresaA);

        assertThat(produtosAtivos)
                .extracting(Produto::getId)
                .containsExactly(arroz.getId(), feijao.getId());
    }

    @Test
    void findByEmpresaAndAtivoTrueAndNomeContainingIgnoreCaseOrderByNomeAscShouldSearchInsideEmpresaOnly() {
        Empresa empresaA = persistEmpresa("Emporio A", "99999999000199");
        Empresa empresaB = persistEmpresa("Emporio B", "10101010000110");

        Categoria categoriaA = persistCategoria(empresaA, "Graos");
        Categoria categoriaB = persistCategoria(empresaB, "Graos");

        Produto arrozBranco = persistProduto(empresaA, categoriaA, "Arroz Branco", true, 12, 4);
        persistProduto(empresaA, categoriaA, "Arroz Integral", false, 7, 3);
        persistProduto(empresaB, categoriaB, "Arroz Tipo 1", true, 6, 2);
        entityManager.clear();

        List<Produto> encontrados = produtoRepository
                .findByEmpresaAndAtivoTrueAndNomeContainingIgnoreCaseOrderByNomeAsc(empresaA, "arroz");

        assertThat(encontrados)
                .extracting(Produto::getId)
                .containsExactly(arrozBranco.getId());
    }

    @Test
    void findEstoqueBaixoShouldReturnOnlyProdutosWithLowStockFromEmpresa() {
        Empresa empresaA = persistEmpresa("Mercadinho A", "12121212000112");
        Empresa empresaB = persistEmpresa("Mercadinho B", "13131313000113");

        Categoria categoriaA = persistCategoria(empresaA, "Padaria");
        Categoria categoriaB = persistCategoria(empresaB, "Padaria");

        Produto pao = persistProduto(empresaA, categoriaA, "Pao Frances", true, 4, 5);
        persistProduto(empresaA, categoriaA, "Pao Doce", true, 8, 5);
        persistProduto(empresaB, categoriaB, "Pao de Forma", true, 1, 3);
        entityManager.clear();

        List<Produto> estoqueBaixo = produtoRepository.findEstoqueBaixo(empresaA);

        assertThat(estoqueBaixo)
                .extracting(Produto::getId)
                .containsExactly(pao.getId());
    }

    private Empresa persistEmpresa(String nome, String cnpj) {
        Empresa empresa = Empresa.builder()
                .nome(nome)
                .cnpj(cnpj)
                .build();

        return entityManager.persistAndFlush(empresa);
    }

    private Categoria persistCategoria(Empresa empresa, String nome) {
        Categoria categoria = Categoria.builder()
                .empresa(empresa)
                .nome(nome)
                .build();

        return entityManager.persistAndFlush(categoria);
    }

    private Produto persistProduto(
            Empresa empresa,
            Categoria categoria,
            String nome,
            boolean ativo,
            int estoque,
            int estoqueMin
    ) {
        Produto produto = Produto.builder()
                .empresa(empresa)
                .categoria(categoria)
                .nome(nome)
                .modelo(nome)
                .referencia(nome.replace(" ", "-").toUpperCase())
                .gradeGrupo(nome.replace(" ", "-").toUpperCase())
                .cor("Unica")
                .tamanho("UN")
                .codigoBarras("789" + Math.abs(nome.hashCode()))
                .preco(new BigDecimal("9.90"))
                .estoque(estoque)
                .estoqueMin(estoqueMin)
                .ativo(ativo)
                .build();

        return entityManager.persistAndFlush(produto);
    }
}
