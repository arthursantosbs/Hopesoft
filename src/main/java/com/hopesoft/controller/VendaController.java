package com.hopesoft.controller;

import com.hopesoft.dto.ItemVendaRequest;
import com.hopesoft.dto.VendaRequest;
import com.hopesoft.dto.VendaResponse;
import com.hopesoft.model.Empresa;
import com.hopesoft.model.ItemVenda;
import com.hopesoft.model.Produto;
import com.hopesoft.model.Venda;
import com.hopesoft.security.HopesoftUserDetails;
import com.hopesoft.service.ProdutoService;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/vendas")
@RequiredArgsConstructor
public class VendaController {

    private final VendaService vendaService;
    private final ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<VendaResponse> registrarVenda(
            @Valid @RequestBody VendaRequest request,
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        // Criar a venda
        Venda venda = new Venda();
        venda.setFormaPagamento(request.formaPagamento());

        // Obter a empresa do usuário autenticado
        Empresa empresa = new Empresa();
        empresa.setId(userDetails.getEmpresaId());
        venda.setEmpresa(empresa);

        // Obter o usuário do contexto de autenticação
        org.springframework.security.core.userdetails.UserDetails user = userDetails;
        com.hopesoft.model.Usuario usuario = new com.hopesoft.model.Usuario();
        usuario.setId(userDetails.getId());
        venda.setUsuario(usuario);

        // Converter ItemVendaRequest em ItemVenda
        List<ItemVenda> itens = request.itens().stream()
                .map(itemRequest -> {
                    ItemVenda itemVenda = new ItemVenda();
                    Produto produto = new Produto();
                    produto.setId(itemRequest.produtoId());
                    itemVenda.setProduto(produto);
                    itemVenda.setQuantidade(itemRequest.quantidade());
                    return itemVenda;
                })
                .toList();

        venda.setItens(itens);

        // Registrar a venda
        Venda vendaRegistrada = vendaService.registrarVenda(venda);

        // Calcular troco se necessário
        if (request.valorRecebido() != null) {
            BigDecimal troco = vendaService.calcularTroco(vendaRegistrada, request.valorRecebido());
            vendaRegistrada.setTroco(troco);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(VendaResponse.fromEntity(vendaRegistrada));
    }
}

