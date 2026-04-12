package com.hopesoft.repository;

import com.hopesoft.model.Empresa;
import com.hopesoft.model.PagamentoVenda;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PagamentoVendaRepository extends JpaRepository<PagamentoVenda, Long> {

    @Query("""
            SELECT p.formaPagamento AS formaPagamento, COALESCE(SUM(p.valor), 0) AS total
            FROM PagamentoVenda p
            JOIN p.venda v
            WHERE v.empresa = :empresa
              AND v.criadoEm >= :inicio
              AND v.criadoEm <= :fim
              AND v.status = com.hopesoft.model.StatusVenda.FINALIZADA
            GROUP BY p.formaPagamento
            """)
    List<TotalPorFormaPagamentoProjection> sumTotalByFormaPagamento(
            @Param("empresa") Empresa empresa,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );
}
