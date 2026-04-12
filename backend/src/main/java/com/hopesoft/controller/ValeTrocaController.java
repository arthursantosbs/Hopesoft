package com.hopesoft.controller;

import com.hopesoft.dto.ValeTrocaResponse;
import com.hopesoft.security.HopesoftUserDetails;
import com.hopesoft.service.ValeTrocaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vales-troca")
@RequiredArgsConstructor
public class ValeTrocaController {

    private final ValeTrocaService valeTrocaService;

    @GetMapping
    public ResponseEntity<List<ValeTrocaResponse>> listarAtivos(
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        return ResponseEntity.ok(
                valeTrocaService.listarValesAtivos(userDetails.getEmpresaId()).stream()
                        .map(ValeTrocaResponse::fromEntity)
                        .toList()
        );
    }
}
