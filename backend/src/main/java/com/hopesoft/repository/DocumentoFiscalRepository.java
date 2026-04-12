package com.hopesoft.repository;

import com.hopesoft.model.DocumentoFiscal;
import com.hopesoft.model.Empresa;
import com.hopesoft.model.Venda;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentoFiscalRepository extends JpaRepository<DocumentoFiscal, Long> {
    List<DocumentoFiscal> findByEmpresaOrderByCriadoEmDesc(Empresa empresa);
    Optional<DocumentoFiscal> findFirstByVendaOrderByCriadoEmDesc(Venda venda);
}
