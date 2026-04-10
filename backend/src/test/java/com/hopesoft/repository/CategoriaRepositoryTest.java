package com.hopesoft.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.hopesoft.model.Categoria;
import com.hopesoft.model.Empresa;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

@DataJpaTest
class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void existsByNomeAndEmpresaShouldRespectEmpresaContext() {
        Empresa empresaA = persistEmpresa("Loja A", "33333333000133");
        Empresa empresaB = persistEmpresa("Loja B", "44444444000144");

        entityManager.persistAndFlush(Categoria.builder().empresa(empresaA).nome("Bebidas").build());
        entityManager.persistAndFlush(Categoria.builder().empresa(empresaB).nome("Bebidas").build());

        assertThat(categoriaRepository.existsByNomeAndEmpresa("Bebidas", empresaA)).isTrue();
        assertThat(categoriaRepository.existsByNomeAndEmpresa("Bebidas", empresaB)).isTrue();
        assertThat(categoriaRepository.existsByNomeAndEmpresa("Limpeza", empresaA)).isFalse();
    }

    @Test
    void findByEmpresaOrderByNomeAscShouldReturnOnlyCategoriasFromRequestedEmpresa() {
        Empresa empresaA = persistEmpresa("Loja Centro", "55555555000155");
        Empresa empresaB = persistEmpresa("Loja Bairro", "66666666000166");

        Categoria bebidas = entityManager.persistAndFlush(
                Categoria.builder().empresa(empresaA).nome("Bebidas").build()
        );
        Categoria limpeza = entityManager.persistAndFlush(
                Categoria.builder().empresa(empresaA).nome("Limpeza").build()
        );
        entityManager.persistAndFlush(Categoria.builder().empresa(empresaB).nome("Bebidas").build());
        entityManager.clear();

        List<Categoria> categorias = categoriaRepository.findByEmpresaOrderByNomeAsc(empresaA);

        assertThat(categorias)
                .extracting(Categoria::getId)
                .containsExactly(bebidas.getId(), limpeza.getId());
    }

    private Empresa persistEmpresa(String nome, String cnpj) {
        Empresa empresa = Empresa.builder()
                .nome(nome)
                .cnpj(cnpj)
                .build();

        return entityManager.persistAndFlush(empresa);
    }
}
