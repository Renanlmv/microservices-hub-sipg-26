package com.github.renanlmv.ms.pedido.dto;

import com.github.renanlmv.ms.pedido.entities.ItemDoPedido;
import com.github.renanlmv.ms.pedido.entities.Pedido;
import com.github.renanlmv.ms.pedido.entities.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PedidoResponseDTO {

    private Long id;

    private String nome;

    private String cpf;

    private LocalDate data;

    private Status status;

    private BigDecimal valorTotal;

    private List<@Valid ItemDoPedidoDTO> itens = new ArrayList<>();

    public PedidoResponseDTO(Pedido pedido) {
        id = pedido.getId();
        nome = pedido.getNome();
        cpf = pedido.getCpf();
        data = pedido.getData();
        status = pedido.getStatus();
        valorTotal = pedido.getValorTotal();

        for (ItemDoPedido item : pedido.getItens()) {
            ItemDoPedidoDTO itemDTO = new ItemDoPedidoDTO(item);
            itens.add(itemDTO);
        }
    }
}
