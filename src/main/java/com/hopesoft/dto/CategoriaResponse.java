package com.hopesoft.dto;

import com.hopesoft.model.Categoria;

public record CategoriaResponse(
        Long id,
        String nome
) {

    public static CategoriaResponse fromEntity(Categoria categoria) {
        if (categoria == null) {
            return null;
        }

        return new CategoriaResponse(categoria.getId(), categoria.getNome());
    }
}
