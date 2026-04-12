package com.hopesoft.service;

import com.hopesoft.model.Produto;
import org.springframework.stereotype.Service;

@Service
public class EtiquetaService {

    public String gerarZpl(Produto produto) {
        return """
                ^XA
                ^CF0,28
                ^FO40,40^FD%s^FS
                ^CF0,22
                ^FO40,85^FDRef: %s %s %s^FS
                ^FO40,120^FDPreco: R$ %s^FS
                ^BY2,3,60
                ^FO40,165^BCN,60,Y,N,N^FD%s^FS
                ^XZ
                """.formatted(
                produto.getNome(),
                produto.getReferencia(),
                produto.getCor() == null ? "" : produto.getCor(),
                produto.getTamanho() == null ? "" : produto.getTamanho(),
                produto.getPreco(),
                produto.getCodigoBarras()
        ).trim();
    }

    public String gerarEpl(Produto produto) {
        return """
                N
                A40,20,0,4,1,1,N,"%s"
                A40,60,0,3,1,1,N,"Ref: %s %s %s"
                A40,90,0,3,1,1,N,"Preco: R$ %s"
                B40,130,0,E30,2,4,60,B,"%s"
                P1
                """.formatted(
                produto.getNome(),
                produto.getReferencia(),
                produto.getCor() == null ? "" : produto.getCor(),
                produto.getTamanho() == null ? "" : produto.getTamanho(),
                produto.getPreco(),
                produto.getCodigoBarras()
        ).trim();
    }
}
