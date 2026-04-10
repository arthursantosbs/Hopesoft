package com.hopesoft.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.hopesoft.model.Empresa;
import com.hopesoft.model.FormaPagamento;
import com.hopesoft.model.Perfil;
import com.hopesoft.model.Usuario;
import com.hopesoft.model.Venda;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

@DataJpaTest
class VendaRepositoryTest {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByEmpresaAndCriadoEmBetweenOrderByCriadoEmDescShouldFilterAndSortSales() {
        Empresa empresaA = persistEmpresa("Caixa A", "14141414000114");
        Empresa empresaB = persistEmpresa("Caixa B", "15151515000115");
        Usuario usuarioA = persistUsuario(empresaA, "admin-a@hopesoft.com");
        Usuario usuarioB = persistUsuario(empresaB, "admin-b@hopesoft.com");

        LocalDateTime inicio = LocalDateTime.of(2026, 4, 3, 9, 0);
        LocalDateTime meio = LocalDateTime.of(2026, 4, 3, 11, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 4, 3, 13, 0);

        Venda vendaMaisAntiga = persistVenda(empresaA, usuarioA, new BigDecimal("10.00"), inicio.plusMinutes(30));
        Venda vendaMaisRecente = persistVenda(empresaA, usuarioA, new BigDecimal("20.00"), meio.plusMinutes(45));
        persistVenda(empresaB, usuarioB, new BigDecimal("99.00"), meio);
        entityManager.clear();

        List<Venda> vendas = vendaRepository.findByEmpresaAndCriadoEmBetweenOrderByCriadoEmDesc(empresaA, inicio, fim);

        assertThat(vendas)
                .extracting(Venda::getId)
                .containsExactly(vendaMaisRecente.getId(), vendaMaisAntiga.getId());
    }

    @Test
    void sumTotalByEmpresaAndPeriodoShouldSumOnlyMatchingSales() {
        Empresa empresaA = persistEmpresa("PDV A", "16161616000116");
        Empresa empresaB = persistEmpresa("PDV B", "17171717000117");
        Usuario usuarioA = persistUsuario(empresaA, "operador-a@hopesoft.com");
        Usuario usuarioB = persistUsuario(empresaB, "operador-b@hopesoft.com");

        LocalDateTime inicio = LocalDateTime.of(2026, 4, 3, 0, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 4, 3, 23, 59);

        persistVenda(empresaA, usuarioA, new BigDecimal("15.50"), LocalDateTime.of(2026, 4, 3, 10, 0));
        persistVenda(empresaA, usuarioA, new BigDecimal("24.50"), LocalDateTime.of(2026, 4, 3, 14, 0));
        persistVenda(empresaB, usuarioB, new BigDecimal("100.00"), LocalDateTime.of(2026, 4, 3, 16, 0));
        entityManager.clear();

        BigDecimal total = vendaRepository.sumTotalByEmpresaAndPeriodo(empresaA, inicio, fim);

        assertThat(total).isEqualByComparingTo("40.00");
    }

    private Empresa persistEmpresa(String nome, String cnpj) {
        Empresa empresa = Empresa.builder()
                .nome(nome)
                .cnpj(cnpj)
                .build();

        return entityManager.persistAndFlush(empresa);
    }

    private Usuario persistUsuario(Empresa empresa, String email) {
        Usuario usuario = Usuario.builder()
                .empresa(empresa)
                .nome("Usuario de teste")
                .email(email)
                .senha("senha-segura")
                .perfil(Perfil.ADMIN)
                .build();

        return entityManager.persistAndFlush(usuario);
    }

    private Venda persistVenda(
            Empresa empresa,
            Usuario usuario,
            BigDecimal total,
            LocalDateTime criadoEm
    ) {
        Venda venda = Venda.builder()
                .empresa(empresa)
                .usuario(usuario)
                .total(total)
                .formaPagamento(FormaPagamento.DINHEIRO)
                .build();

        Venda persisted = entityManager.persistAndFlush(venda);
        persisted.setCriadoEm(criadoEm);
        entityManager.flush();
        return persisted;
    }
}
