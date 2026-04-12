package com.hopesoft.dto;

import java.util.List;

public record ProdutoGradeResponse(
        String gradeGrupo,
        String modelo,
        String referencia,
        List<ProdutoResponse> variantes
) {
}
