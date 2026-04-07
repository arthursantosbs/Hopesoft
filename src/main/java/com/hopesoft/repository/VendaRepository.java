package com.hopesoft.repository;

import com.hopesoft.model.Empresa;
import com.hopesoft.model.FormaPagamento;
import com.hopesoft.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface VendaRepository extends JpaRepository<Venda, Long> {
    List<Venda> findByEmpresaAndCriadoEmBetweenOrderByCriadoEmDesc(
            Empresa empresa, LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venda v WHERE v.empresa = :empresa AND v.criadoEm >= :inicio AND v.criadoEm <= :fim")
    BigDecimal sumTotalByEmpresaAndPeriodo(
            @Param("empresa") Empresa empresa,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    @Query("SELECT COUNT(v) FROM Venda v WHERE v.empresa = :empresa AND v.criadoEm >= :inicio AND v.criadoEm <= :fim")
    Long countByEmpresaAndPeriodo(
            @Param("empresa") Empresa empresa,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    @Query("SELECT v.formaPagamento, COALESCE(SUM(v.total), 0) FROM Venda v WHERE v.empresa = :empresa AND v.criadoEm >= :inicio AND v.criadoEm <= :fim GROUP BY v.formaPagamento")
    Map<FormaPagamento, BigDecimal> sumTotalByFormaPagamento(
            @Param("empresa") Empresa empresa,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);
}
