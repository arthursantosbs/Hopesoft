package com.hopesoft.repository;

import com.hopesoft.model.CaixaSessao;
import com.hopesoft.model.Empresa;
import com.hopesoft.model.StatusCaixaSessao;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaixaSessaoRepository extends JpaRepository<CaixaSessao, Long> {
    Optional<CaixaSessao> findFirstByEmpresaAndStatusOrderByAbertoEmDesc(Empresa empresa, StatusCaixaSessao status);
    List<CaixaSessao> findByEmpresaOrderByAbertoEmDesc(Empresa empresa);
}
