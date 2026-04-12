package com.hopesoft.repository;

import com.hopesoft.model.Empresa;
import com.hopesoft.model.StatusVenda;
import com.hopesoft.model.Venda;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VendaRepository extends JpaRepository<Venda, Long> {
    List<Venda> findByEmpresaAndCriadoEmBetweenOrderByCriadoEmDesc(
            Empresa empresa,
            LocalDateTime inicio,
            LocalDateTime fim
    );
    List<Venda> findByEmpresaAndStatusOrderByCriadoEmDesc(Empresa empresa, StatusVenda status);
    Optional<Venda> findByIdAndEmpresa(Empresa empresa, Long id);

    @Query("""
            SELECT COALESCE(SUM(v.total), 0)
            FROM Venda v
            WHERE v.empresa = :empresa
              AND v.criadoEm >= :inicio
              AND v.criadoEm <= :fim
              AND v.status = com.hopesoft.model.StatusVenda.FINALIZADA
            """)
    BigDecimal sumTotalByEmpresaAndPeriodo(
            @Param("empresa") Empresa empresa,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

    @Query("""
            SELECT COUNT(v)
            FROM Venda v
            WHERE v.empresa = :empresa
              AND v.criadoEm >= :inicio
              AND v.criadoEm <= :fim
              AND v.status = com.hopesoft.model.StatusVenda.FINALIZADA
            """)
    Long countByEmpresaAndPeriodo(
            @Param("empresa") Empresa empresa,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );
}
