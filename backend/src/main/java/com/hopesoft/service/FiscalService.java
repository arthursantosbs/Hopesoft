package com.hopesoft.service;

import com.hopesoft.model.DocumentoFiscal;
import com.hopesoft.model.Empresa;
import com.hopesoft.model.StatusDocumentoFiscal;
import com.hopesoft.model.TipoDocumentoFiscal;
import com.hopesoft.model.Venda;
import com.hopesoft.repository.DocumentoFiscalRepository;
import com.hopesoft.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FiscalService {

    private final DocumentoFiscalRepository documentoFiscalRepository;
    private final EmpresaService empresaService;
    private final VendaRepository vendaRepository;

    @Transactional
    public DocumentoFiscal emitirDocumento(Long empresaId, Long vendaId, TipoDocumentoFiscal tipoDocumentoFiscal) {
        Empresa empresa = empresaService.buscarEmpresaPorId(empresaId);
        Venda venda = vendaRepository.findByIdAndEmpresa(empresa, vendaId)
                .orElseThrow(() -> new IllegalArgumentException("Venda nao encontrada para emissao fiscal"));

        DocumentoFiscal documentoFiscal = DocumentoFiscal.builder()
                .empresa(empresa)
                .venda(venda)
                .tipoDocumento(tipoDocumentoFiscal)
                .status(StatusDocumentoFiscal.PENDENTE_CONFIGURACAO)
                .mensagemRetorno("Integracao fiscal preparada. Configure certificado, ambiente SEFAZ e homologacao para emissao real.")
                .payloadGerado("""
                        {
                          "vendaId": %d,
                          "tipoDocumento": "%s",
                          "total": %s,
                          "empresaId": %d
                        }
                        """.formatted(venda.getId(), tipoDocumentoFiscal.name(), venda.getTotal(), empresa.getId()).trim())
                .build();

        return documentoFiscalRepository.save(documentoFiscal);
    }
}
