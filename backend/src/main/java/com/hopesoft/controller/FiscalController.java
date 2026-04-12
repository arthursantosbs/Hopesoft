package com.hopesoft.controller;

import com.hopesoft.dto.DocumentoFiscalResponse;
import com.hopesoft.model.DocumentoFiscal;
import com.hopesoft.model.TipoDocumentoFiscal;
import com.hopesoft.security.HopesoftUserDetails;
import com.hopesoft.service.FiscalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fiscal")
@RequiredArgsConstructor
public class FiscalController {

    private final FiscalService fiscalService;

    @PostMapping("/vendas/{vendaId}/emitir")
    public ResponseEntity<DocumentoFiscalResponse> emitirDocumento(
            @PathVariable Long vendaId,
            @RequestParam(defaultValue = "NFCE") TipoDocumentoFiscal tipoDocumento,
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        DocumentoFiscal documentoFiscal = fiscalService.emitirDocumento(userDetails.getEmpresaId(), vendaId, tipoDocumento);
        return ResponseEntity.status(HttpStatus.CREATED).body(DocumentoFiscalResponse.fromEntity(documentoFiscal));
    }
}
