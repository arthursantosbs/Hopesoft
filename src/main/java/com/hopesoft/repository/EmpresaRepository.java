package com.hopesoft.repository;

import com.hopesoft.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    boolean existsByCnpj(String cnpj);
}