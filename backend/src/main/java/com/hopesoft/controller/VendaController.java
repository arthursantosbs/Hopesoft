package com.hopesoft.controller;

import com.hopesoft.dto.ComprovanteResponse;
import com.hopesoft.dto.ValeTrocaRequest;
import com.hopesoft.dto.ValeTrocaResponse;
import com.hopesoft.dto.VendaRequest;
import com.hopesoft.dto.VendaResponse;
import com.hopesoft.model.ValeTroca;
import com.hopesoft.model.Venda;
import com.hopesoft.security.HopesoftUserDetails;
import com.hopesoft.service.ComprovanteService;
import com.hopesoft.service.ValeTrocaService;
import com.hopesoft.service.VendaService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vendas")
@RequiredArgsConstructor
public class VendaController {

    private final VendaService vendaService;
    private final ValeTrocaService valeTrocaService;
    private final ComprovanteService comprovanteService;

    @GetMapping
    public ResponseEntity<List<VendaResponse>> listarVendas(
            @RequestParam(required = false) LocalDate data,
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        List<VendaResponse> vendas = vendaService.listarVendasDoDia(userDetails.getEmpresaId(), data)
                .stream()
                .map(VendaResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(vendas);
    }

    @GetMapping("/em-espera")
    public ResponseEntity<List<VendaResponse>> listarVendasEmEspera(
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        List<VendaResponse> vendas = vendaService.listarVendasEmEspera(userDetails.getEmpresaId())
                .stream()
                .map(VendaResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(vendas);
    }

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

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<VendaResponse> finalizarVendaEmEspera(
            @PathVariable Long id,
            @Valid @RequestBody VendaRequest request,
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        Venda venda = vendaService.finalizarVendaEmEspera(userDetails.getEmpresaId(), userDetails.getId(), id, request);
        return ResponseEntity.ok(VendaResponse.fromEntity(venda));
    }

    @DeleteMapping("/{id}/em-espera")
    public ResponseEntity<VendaResponse> cancelarVendaEmEspera(
            @PathVariable Long id,
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        Venda venda = vendaService.cancelarVendaEmEspera(userDetails.getEmpresaId(), id);
        return ResponseEntity.ok(VendaResponse.fromEntity(venda));
    }

    @PostMapping("/{id}/vale-troca")
    public ResponseEntity<ValeTrocaResponse> gerarValeTroca(
            @PathVariable Long id,
            @Valid @RequestBody ValeTrocaRequest request,
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        ValeTroca valeTroca = valeTrocaService.gerarValeTroca(userDetails.getEmpresaId(), id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ValeTrocaResponse.fromEntity(valeTroca));
    }

    @GetMapping("/{id}/comprovante")
    public ResponseEntity<ComprovanteResponse> gerarComprovante(
            @PathVariable Long id,
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        Venda venda = vendaService.buscarVendaPorId(userDetails.getEmpresaId(), id);
        return ResponseEntity.ok(new ComprovanteResponse("TEXT", comprovanteService.gerarComprovanteTermico(venda)));
    }
}
