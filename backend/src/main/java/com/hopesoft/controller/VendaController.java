package com.hopesoft.controller;

import com.hopesoft.dto.VendaRequest;
import com.hopesoft.dto.VendaResponse;
import com.hopesoft.model.Venda;
import com.hopesoft.security.HopesoftUserDetails;
import com.hopesoft.service.VendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vendas")
@RequiredArgsConstructor
public class VendaController {

    private final VendaService vendaService;

    @PostMapping
    public ResponseEntity<VendaResponse> registrarVenda(
            @Valid @RequestBody VendaRequest request,
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        Venda vendaRegistrada = vendaService.registrarVenda(
                userDetails.getEmpresaId(),
                userDetails.getId(),
                request
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(VendaResponse.fromEntity(vendaRegistrada));
    }
}
