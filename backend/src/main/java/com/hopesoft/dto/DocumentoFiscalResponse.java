package com.hopesoft.dto;

import com.hopesoft.model.DocumentoFiscal;
import java.time.LocalDateTime;

public record DocumentoFiscalResponse(
        Long id,
        String tipoDocumento,
        String status,
        String chaveAcesso,
        String protocolo,
        String mensagemRetorno,
        String payloadGerado,
        Long vendaId,
        LocalDateTime criadoEm
) {

    public static DocumentoFiscalResponse fromEntity(DocumentoFiscal documentoFiscal) {
        return new DocumentoFiscalResponse(
                documentoFiscal.getId(),
                documentoFiscal.getTipoDocumento().name(),
                documentoFiscal.getStatus().name(),
                documentoFiscal.getChaveAcesso(),
                documentoFiscal.getProtocolo(),
                documentoFiscal.getMensagemRetorno(),
                documentoFiscal.getPayloadGerado(),
                documentoFiscal.getVenda().getId(),
                documentoFiscal.getCriadoEm()
        );
    }
}
