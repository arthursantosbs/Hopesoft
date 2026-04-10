package com.hopesoft.repository;

import com.hopesoft.model.Empresa;
import com.hopesoft.model.Produto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByEmpresaAndAtivoTrueOrderByNomeAsc(Empresa empresa);
    List<Produto> findByEmpresaAndAtivoTrueAndNomeContainingIgnoreCaseOrderByNomeAsc(Empresa empresa, String nome);
    List<Produto> findByEmpresaOrderByNomeAsc(Empresa empresa);
    Optional<Produto> findByIdAndEmpresa(Long id, Empresa empresa);
    Optional<Produto> findByIdAndEmpresaAndAtivoTrue(Long id, Empresa empresa);

    @Query("""
            SELECT p
            FROM Produto p
            WHERE p.empresa = :empresa
              AND p.ativo = true
              AND p.estoque <= p.estoqueMin
            ORDER BY p.nome
            """)
    List<Produto> findEstoqueBaixo(@Param("empresa") Empresa empresa);
}
