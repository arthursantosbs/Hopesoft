package com.hopesoft.controller;

import com.hopesoft.dto.CaixaAberturaRequest;
import com.hopesoft.dto.CaixaFechamentoRequest;
import com.hopesoft.dto.CaixaMovimentoRequest;
import com.hopesoft.dto.CaixaSessaoResponse;
import com.hopesoft.security.HopesoftUserDetails;
import com.hopesoft.service.CaixaService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/caixa")
@RequiredArgsConstructor
public class CaixaController {

    private final CaixaService caixaService;

    @GetMapping("/sessao-atual")
    public ResponseEntity<CaixaSessaoResponse> sessaoAtual(
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        return caixaService.buscarSessaoAtual(userDetails.getEmpresaId())
                .map(CaixaSessaoResponse::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/historico")
    public ResponseEntity<List<CaixaSessaoResponse>> historico(
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        List<CaixaSessaoResponse> sessoes = caixaService.listarSessoes(userDetails.getEmpresaId())
                .stream()
                .map(CaixaSessaoResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(sessoes);
    }

    @PostMapping("/abrir")
    public ResponseEntity<CaixaSessaoResponse> abrir(
            @Valid @RequestBody CaixaAberturaRequest request,
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                CaixaSessaoResponse.fromEntity(
                        caixaService.abrirSessao(userDetails.getEmpresaId(), userDetails.getId(), request)
                )
        );
    }

    @PostMapping("/sangria")
    public ResponseEntity<CaixaSessaoResponse> sangria(
            @Valid @RequestBody CaixaMovimentoRequest request,
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        return ResponseEntity.ok(
                CaixaSessaoResponse.fromEntity(
                        caixaService.registrarSangria(userDetails.getEmpresaId(), userDetails.getId(), request)
                )
        );
    }

    @PostMapping("/suprimento")
    public ResponseEntity<CaixaSessaoResponse> suprimento(
            @Valid @RequestBody CaixaMovimentoRequest request,
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        return ResponseEntity.ok(
                CaixaSessaoResponse.fromEntity(
                        caixaService.registrarSuprimento(userDetails.getEmpresaId(), userDetails.getId(), request)
                )
        );
    }

    @PostMapping("/fechar")
    public ResponseEntity<CaixaSessaoResponse> fechar(
            @Valid @RequestBody CaixaFechamentoRequest request,
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        return ResponseEntity.ok(
                CaixaSessaoResponse.fromEntity(
                        caixaService.fecharSessao(userDetails.getEmpresaId(), userDetails.getId(), request)
                )
        );
    }
}
