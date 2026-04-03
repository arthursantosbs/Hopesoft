package com.hopesoft.repository;

import com.hopesoft.model.Empresa;
import com.hopesoft.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByEmpresaAndAtivoTrue(Empresa empresa);
    List<Produto> findByEmpresaAndNomeContainingIgnoreCase(Empresa empresa, String nome);
    List<Produto> findByEmpresa(Empresa empresa);

    @Query("SELECT p FROM Produto p WHERE p.estoque <= p.estoqueMin AND p.empresa = :empresa")
    List<Produto> findEstoqueBaixo(@Param("empresa") Empresa empresa);
}
