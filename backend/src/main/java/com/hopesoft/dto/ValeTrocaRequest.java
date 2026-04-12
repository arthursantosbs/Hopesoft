package com.hopesoft.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ValeTrocaRequest(
        @NotEmpty(message = "Informe ao menos um item para devolucao")
        List<@Valid ValeTrocaItemRequest> itens,

        String nomeCliente,
        String documentoCliente,
        String observacao
) {
}
