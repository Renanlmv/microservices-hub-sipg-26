package com.github.renanlmv.ms.pagamento.tests;

import com.github.renanlmv.ms.pagamento.entities.Pagamento;
import com.github.renanlmv.ms.pagamento.entities.Status;

import java.math.BigDecimal;

// classe para instanciar objetos
public class Factory {

    public static Pagamento createPagamento() {
        Pagamento pagamento = new Pagamento(1L, BigDecimal.valueOf(32.25), "Brienne de Tarth", "3654789650152365", "07/15", "354", Status.CRIADO, 1L);
        return pagamento;
    }

    public static Pagamento createPagamentoSemId() {
        Pagamento pagamento = createPagamento();
        pagamento.setId(null);
        return pagamento;
    }
}
