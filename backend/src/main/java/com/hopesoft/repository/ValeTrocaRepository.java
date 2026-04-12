package com.hopesoft.repository;

import com.hopesoft.model.Empresa;
import com.hopesoft.model.StatusValeTroca;
import com.hopesoft.model.ValeTroca;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValeTrocaRepository extends JpaRepository<ValeTroca, Long> {
    Optional<ValeTroca> findByCodigoAndEmpresa(String codigo, Empresa empresa);
    List<ValeTroca> findByEmpresaAndStatusOrderByCriadoEmDesc(Empresa empresa, StatusValeTroca status);
}
