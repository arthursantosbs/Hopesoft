package com.hopesoft.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "caixas_sessao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaixaSessao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne
    @JoinColumn(name = "usuario_abertura_id", nullable = false)
    private Usuario usuarioAbertura;

    @ManyToOne
    @JoinColumn(name = "usuario_fechamento_id")
    private Usuario usuarioFechamento;

    @Column(name = "fundo_troco_inicial", nullable = false)
    @Builder.Default
    private BigDecimal fundoTrocoInicial = BigDecimal.ZERO;

    @Column(name = "valor_informado_fechamento")
    private BigDecimal valorInformadoFechamento;

    @Column(name = "valor_esperado_fechamento")
    @Builder.Default
    private BigDecimal valorEsperadoFechamento = BigDecimal.ZERO;

    @Column(name = "total_sangrias", nullable = false)
    @Builder.Default
    private BigDecimal totalSangrias = BigDecimal.ZERO;

    @Column(name = "total_suprimentos", nullable = false)
    @Builder.Default
    private BigDecimal totalSuprimentos = BigDecimal.ZERO;

    @Column(name = "total_vendas_dinheiro", nullable = false)
    @Builder.Default
    private BigDecimal totalVendasDinheiro = BigDecimal.ZERO;

    @Column(name = "total_vendas_pix", nullable = false)
    @Builder.Default
    private BigDecimal totalVendasPix = BigDecimal.ZERO;

    @Column(name = "total_vendas_cartao", nullable = false)
    @Builder.Default
    private BigDecimal totalVendasCartao = BigDecimal.ZERO;

    @Column(name = "total_vendas_fiado", nullable = false)
    @Builder.Default
    private BigDecimal totalVendasFiado = BigDecimal.ZERO;

    @Column(name = "total_vendas_vale_troca", nullable = false)
    @Builder.Default
    private BigDecimal totalVendasValeTroca = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusCaixaSessao status = StatusCaixaSessao.ABERTO;

    @Column(length = 500)
    private String observacao;

    @Column(name = "aberto_em", nullable = false)
    private LocalDateTime abertoEm;

    @Column(name = "fechado_em")
    private LocalDateTime fechadoEm;

    @OneToMany(mappedBy = "caixaSessao")
    private List<MovimentoCaixa> movimentos;

    @PrePersist
    public void prePersist() {
        this.abertoEm = LocalDateTime.now();
    }
}
