package com.hopesoft.repository;

import com.hopesoft.model.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByIdAndEmpresa_Id(Long id, Long empresaId);
    boolean existsByEmail(String email);
}
