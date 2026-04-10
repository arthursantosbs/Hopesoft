package com.hopesoft.repository;

import com.hopesoft.model.FormaPagamento;
import java.math.BigDecimal;

public interface TotalPorFormaPagamentoProjection {
    FormaPagamento getFormaPagamento();
    BigDecimal getTotal();
}
