package com.hopesoft.repository;

import com.hopesoft.model.CaixaSessao;
import com.hopesoft.model.MovimentoCaixa;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentoCaixaRepository extends JpaRepository<MovimentoCaixa, Long> {
    List<MovimentoCaixa> findByCaixaSessaoOrderByCriadoEmDesc(CaixaSessao caixaSessao);
}
