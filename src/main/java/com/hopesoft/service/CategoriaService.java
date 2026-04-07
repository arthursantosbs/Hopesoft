package com.hopesoft.service;

import com.hopesoft.exception.CategoriaNotFoundException;
import com.hopesoft.model.Categoria;
import com.hopesoft.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public Categoria buscarCategoriaPorId(Long empresaId, Long categoriaId) {
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new CategoriaNotFoundException("Categoria não encontrada"));
    }
}
