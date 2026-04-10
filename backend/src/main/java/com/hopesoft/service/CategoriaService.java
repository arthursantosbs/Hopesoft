package com.hopesoft.service;

import com.hopesoft.exception.CategoriaNotFoundException;
import com.hopesoft.model.Categoria;
import com.hopesoft.model.Empresa;
import com.hopesoft.repository.CategoriaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final EmpresaService empresaService;

    public Categoria buscarCategoriaPorId(Long empresaId, Long categoriaId) {
        Empresa empresa = empresaService.buscarEmpresaPorId(empresaId);
        return categoriaRepository.findByIdAndEmpresa(categoriaId, empresa)
                .orElseThrow(() -> new CategoriaNotFoundException("Categoria nao encontrada para a empresa informada"));
    }

    public List<Categoria> listarCategoriasPorEmpresa(Long empresaId) {
        Empresa empresa = empresaService.buscarEmpresaPorId(empresaId);
        return categoriaRepository.findByEmpresaOrderByNomeAsc(empresa);
    }
}
