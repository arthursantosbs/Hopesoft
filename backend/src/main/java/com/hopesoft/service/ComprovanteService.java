package com.hopesoft.service;

import com.hopesoft.model.ItemVenda;
import com.hopesoft.model.PagamentoVenda;
import com.hopesoft.model.Venda;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

@Service
public class ComprovanteService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public String gerarComprovanteTermico(Venda venda) {
        StringBuilder builder = new StringBuilder();
        builder.append("HOPE SOFT PDV\n");
        builder.append("Venda #").append(venda.getId()).append('\n');
        builder.append("Data: ").append(venda.getCriadoEm().format(DATE_TIME_FORMATTER)).append("\n\n");

        for (ItemVenda item : venda.getItens()) {
            builder.append(item.getProduto().getNome());
            if (item.getProduto().getCor() != null || item.getProduto().getTamanho() != null) {
                builder.append(" (")
                        .append(item.getProduto().getCor() == null ? "-" : item.getProduto().getCor())
                        .append("/")
                        .append(item.getProduto().getTamanho() == null ? "-" : item.getProduto().getTamanho())
                        .append(")");
            }
            builder.append('\n');
            builder.append(item.getQuantidade())
                    .append(" x ")
                    .append(item.getPrecoUnit())
                    .append(" = ")
                    .append(item.getSubtotal())
                    .append('\n');
        }

        builder.append("\nSubtotal: ").append(venda.getSubtotalBruto()).append('\n');
        builder.append("Descontos: ").append(venda.getDescontoTotal()).append('\n');
        builder.append("Acrescimos: ").append(venda.getAcrescimoTotal()).append('\n');
        builder.append("TOTAL: ").append(venda.getTotal()).append("\n\n");

        for (PagamentoVenda pagamento : venda.getPagamentos()) {
            builder.append(pagamento.getFormaPagamento().name())
                    .append(": ")
                    .append(pagamento.getValor())
                    .append('\n');
        }

        if (venda.getTroco() != null) {
            builder.append("Troco: ").append(venda.getTroco()).append('\n');
        }

        builder.append("\nObrigado pela preferencia.\n");
        return builder.toString();
    }
}
