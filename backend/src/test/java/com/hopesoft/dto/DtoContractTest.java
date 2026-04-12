package com.hopesoft.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hopesoft.model.Categoria;
import com.hopesoft.model.Empresa;
import com.hopesoft.model.FormaPagamento;
import com.hopesoft.model.ItemVenda;
import com.hopesoft.model.Perfil;
import com.hopesoft.model.Produto;
import com.hopesoft.model.StatusVenda;
import com.hopesoft.model.Usuario;
import com.hopesoft.model.Venda;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DtoContractTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void produtoRequestShouldRejectInvalidFields() {
        ProdutoRequest request = new ProdutoRequest(
                "",
                "",
                "",
                null,
                null,
                "12345678901234567890123456789012345678901234567890123456789012345",
                new BigDecimal("0.00"),
                -1,
                -2,
                0L,
                null
        );

        Set<String> messages = validateMessages(request);

        assertTrue(messages.contains("Nome do produto e obrigatorio"));
        assertTrue(messages.contains("Modelo do produto e obrigatorio"));
        assertTrue(messages.contains("Referencia e obrigatoria"));
        assertTrue(messages.contains("Codigo de barras deve ter no maximo 64 caracteres"));
        assertTrue(messages.contains("Preco deve ser maior que zero"));
        assertTrue(messages.contains("Estoque nao pode ser negativo"));
        assertTrue(messages.contains("Estoque minimo nao pode ser negativo"));
        assertTrue(messages.contains("Categoria deve ser valida"));
    }

    @Test
    void vendaRequestShouldRejectInconsistentCashRules() {
        VendaRequest valorRecebidoEmPix = new VendaRequest(
                List.of(new ItemVendaRequest(10L, 1, null)),
                FormaPagamento.PIX,
                new BigDecimal("50.00"),
                null,
                null,
                null,
                false,
                null,
                null
        );

        Set<String> pixMessages = validateMessages(valorRecebidoEmPix);

        assertTrue(pixMessages.contains("Valor recebido legado so pode ser informado para pagamento em dinheiro"));
    }

    @Test
    void vendaRequestShouldValidateNestedItemsAndRequireAtLeastOne() {
        VendaRequest semItens = new VendaRequest(List.of(), FormaPagamento.PIX, null, null, null, null, false, null, null);
        VendaRequest itemInvalido = new VendaRequest(
                List.of(new ItemVendaRequest(null, 0, null)),
                FormaPagamento.PIX,
                null,
                null,
                null,
                null,
                false,
                null,
                null
        );

        Set<String> semItensMessages = validateMessages(semItens);
        Set<String> itemInvalidoMessages = validateMessages(itemInvalido);

        assertTrue(semItensMessages.contains("Venda deve ter pelo menos um item"));
        assertTrue(itemInvalidoMessages.contains("Produto e obrigatorio"));
        assertTrue(itemInvalidoMessages.contains("Quantidade deve ser maior que zero"));
    }

    @Test
    void requestsValidosShouldPassValidation() {
        ProdutoRequest produtoRequest = new ProdutoRequest(
                "Cafe 500g",
                "Cafe 500g",
                "CAF-500",
                "Marrom",
                "UN",
                "7891234567890",
                new BigDecimal("12.50"),
                20,
                5,
                2L,
                true
        );

        VendaRequest vendaRequest = new VendaRequest(
                List.of(new ItemVendaRequest(7L, 2, BigDecimal.ZERO)),
                FormaPagamento.DINHEIRO,
                new BigDecimal("30.00"),
                null,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                false,
                null,
                null
        );

        assertTrue(validator.validate(produtoRequest).isEmpty());
        assertTrue(validator.validate(vendaRequest).isEmpty());
    }

    @Test
    void responseMappersShouldProjectEntitiesWithoutExposingJpaTypes() {
        Empresa empresa = Empresa.builder()
                .id(1L)
                .nome("Mercadinho Esperanca")
                .cnpj("12345678000199")
                .ativa(true)
                .build();

        Categoria categoria = Categoria.builder()
                .id(3L)
                .empresa(empresa)
                .nome("Bebidas")
                .build();

        Produto produto = Produto.builder()
                .id(9L)
                .empresa(empresa)
                .nome("Refrigerante 2L")
                .modelo("Refrigerante 2L")
                .referencia("REFRIG-2L")
                .gradeGrupo("REFRIG-2L-GRADE")
                .cor("Unico")
                .tamanho("2L")
                .codigoBarras("7890001112223")
                .preco(new BigDecimal("9.50"))
                .estoque(4)
                .estoqueMin(5)
                .categoria(categoria)
                .ativo(true)
                .build();

        Usuario usuario = Usuario.builder()
                .id(8L)
                .empresa(empresa)
                .nome("Caixa 1")
                .email("caixa@hopesoft.com")
                .senha("senha")
                .perfil(Perfil.OPERADOR)
                .build();

        Venda venda = Venda.builder()
                .id(15L)
                .empresa(empresa)
                .usuario(usuario)
                .subtotalBruto(new BigDecimal("19.00"))
                .descontoTotal(BigDecimal.ZERO)
                .acrescimoTotal(BigDecimal.ZERO)
                .total(new BigDecimal("19.00"))
                .formaPagamento(FormaPagamento.PIX)
                .status(StatusVenda.FINALIZADA)
                .troco(BigDecimal.ZERO)
                .criadoEm(LocalDateTime.of(2026, 4, 3, 10, 30))
                .pagamentos(List.of())
                .build();

        ItemVenda itemVenda = ItemVenda.builder()
                .id(100L)
                .venda(venda)
                .produto(produto)
                .quantidade(2)
                .precoUnit(new BigDecimal("9.50"))
                .subtotalBruto(new BigDecimal("19.00"))
                .descontoValor(BigDecimal.ZERO)
                .acrescimoValor(BigDecimal.ZERO)
                .quantidadeDevolvida(0)
                .subtotal(new BigDecimal("19.00"))
                .build();

        venda.setItens(List.of(itemVenda));

        ProdutoResponse produtoResponse = ProdutoResponse.fromEntity(produto);
        VendaResponse vendaResponse = VendaResponse.fromEntity(venda);
        RelatorioDiaResponse relatorioDiaResponse = new RelatorioDiaResponse(
                LocalDate.of(2026, 4, 3),
                new BigDecimal("190.00"),
                12,
                List.of(
                        new RelatorioFormaPagamentoResponse("PIX", new BigDecimal("100.00")),
                        new RelatorioFormaPagamentoResponse("DINHEIRO", new BigDecimal("90.00"))
                )
        );

        assertEquals(1L, produtoResponse.empresaId());
        assertTrue(produtoResponse.estoqueBaixo());
        assertEquals("Bebidas", produtoResponse.categoria().nome());
        assertEquals("Refrigerante 2L", produtoResponse.modelo());

        assertEquals("PIX", vendaResponse.formaPagamento());
        assertEquals("Caixa 1", vendaResponse.usuarioNome());
        assertEquals(1, vendaResponse.itens().size());
        assertEquals("Refrigerante 2L", vendaResponse.itens().getFirst().produtoNome());

        assertEquals(LocalDate.of(2026, 4, 3), relatorioDiaResponse.data());
        assertEquals(2, relatorioDiaResponse.totaisPorFormaPagamento().size());
        assertEquals(new BigDecimal("190.00"), relatorioDiaResponse.total());
    }

    @Test
    void categoriaResponseShouldReturnNullWhenCategoriaIsAbsent() {
        assertNull(CategoriaResponse.fromEntity(null));
        assertFalse(CategoriaResponse.fromEntity(
                Categoria.builder().id(1L).nome("Limpeza").build()
        ).nome().isBlank());
    }

    private Set<String> validateMessages(Object target) {
        return validator.validate(target)
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
    }
}
