package com.github.renanlmv.ms.pagamento.dto;

import com.github.renanlmv.ms.pagamento.entities.Pagamento;
import com.github.renanlmv.ms.pagamento.entities.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PagamentoResponseDTO {

    private Long id;

    private Long pedidoId;

    private BigDecimal valor;

    private Status status;

    public PagamentoResponseDTO(Pagamento pagamento) {
        id = pagamento.getId();
        pedidoId = pagamento.getPedidoId();
        valor = pagamento.getValor();
        status = pagamento.getStatus();
    }

}
