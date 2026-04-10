package com.hopesoft.controller;

import com.hopesoft.dto.CategoriaResponse;
import com.hopesoft.model.Categoria;
import com.hopesoft.security.HopesoftUserDetails;
import com.hopesoft.service.CategoriaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listar(
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        List<Categoria> categorias = categoriaService.listarCategoriasPorEmpresa(userDetails.getEmpresaId());
        return ResponseEntity.ok(categorias.stream().map(CategoriaResponse::fromEntity).toList());
    }
}
