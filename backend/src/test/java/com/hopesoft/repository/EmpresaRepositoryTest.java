package com.hopesoft.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.hopesoft.model.Empresa;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

@DataJpaTest
class EmpresaRepositoryTest {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void existsByCnpjShouldReturnTrueWhenEmpresaExists() {
        Empresa empresa = Empresa.builder()
                .nome("Mercado Central")
                .cnpj("12345678000199")
                .build();

        entityManager.persistAndFlush(empresa);

        assertThat(empresaRepository.existsByCnpj("12345678000199")).isTrue();
        assertThat(empresaRepository.existsByCnpj("00999999000100")).isFalse();
    }
}
