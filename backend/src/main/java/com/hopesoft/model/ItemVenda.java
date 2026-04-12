package com.hopesoft.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "itens_venda")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "venda_id", nullable = false)
    private Venda venda;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "subtotal_bruto", nullable = false)
    private BigDecimal subtotalBruto;

    @Column(name = "desconto_valor", nullable = false)
    @Builder.Default
    private BigDecimal descontoValor = BigDecimal.ZERO;

    @Column(name = "acrescimo_valor", nullable = false)
    @Builder.Default
    private BigDecimal acrescimoValor = BigDecimal.ZERO;

    @Column(name = "preco_unit", nullable = false)
    private BigDecimal precoUnit;

    @Column(nullable = false)
    private BigDecimal subtotal;

    @Column(name = "quantidade_devolvida", nullable = false)
    @Builder.Default
    private Integer quantidadeDevolvida = 0;
}
