package com.hopesoft.repository;

import com.hopesoft.model.Categoria;
import com.hopesoft.model.Empresa;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    boolean existsByNomeAndEmpresa(String nome, Empresa empresa);
    List<Categoria> findByEmpresaOrderByNomeAsc(Empresa empresa);
    Optional<Categoria> findByIdAndEmpresa(Long id, Empresa empresa);
}
