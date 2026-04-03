package com.hopesoft.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.hopesoft.model.Empresa;
import com.hopesoft.model.Perfil;
import com.hopesoft.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

@DataJpaTest
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByEmailShouldReturnUsuarioWhenEmailExists() {
        Empresa empresa = persistEmpresa("Loja Azul", "11111111000111");
        Usuario usuario = Usuario.builder()
                .empresa(empresa)
                .nome("Admin HopeSoft")
                .email("admin@hopesoft.com")
                .senha("senha-segura")
                .perfil(Perfil.ADMIN)
                .build();

        entityManager.persistAndFlush(usuario);

        assertThat(usuarioRepository.findByEmail("admin@hopesoft.com"))
                .hasValueSatisfying(found -> {
                    assertThat(found.getNome()).isEqualTo("Admin HopeSoft");
                    assertThat(found.getEmpresa().getId()).isEqualTo(empresa.getId());
                });
    }

    @Test
    void existsByEmailShouldReturnTrueOnlyForPersistedEmail() {
        Empresa empresa = persistEmpresa("Loja Verde", "22222222000122");
        Usuario usuario = Usuario.builder()
                .empresa(empresa)
                .nome("Operador")
                .email("operador@hopesoft.com")
                .senha("senha-segura")
                .perfil(Perfil.OPERADOR)
                .build();

        entityManager.persistAndFlush(usuario);

        assertThat(usuarioRepository.existsByEmail("operador@hopesoft.com")).isTrue();
        assertThat(usuarioRepository.existsByEmail("naoexiste@hopesoft.com")).isFalse();
    }

    private Empresa persistEmpresa(String nome, String cnpj) {
        Empresa empresa = Empresa.builder()
                .nome(nome)
                .cnpj(cnpj)
                .build();

        return entityManager.persistAndFlush(empresa);
    }
}
