package com.hopesoft.repository;

import com.hopesoft.model.Categoria;
import com.hopesoft.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    boolean existsByNomeAndEmpresa(String nome, Empresa empresa);
    List<Categoria> findByEmpresa(Empresa empresa);
}
