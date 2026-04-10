package com.github.renanlmv.ms.pagamento.dto;

import com.github.renanlmv.ms.pagamento.entities.Pagamento;
import com.github.renanlmv.ms.pagamento.entities.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PagamentoDTO {

    private Long id;

    @NotNull(message = "Campo valor é requerido")
    @Positive(message = "O valor do pagamento deve ser um número positivo")
    private BigDecimal valor;

    @NotBlank(message = "Campo nome é requerido")
    @Size(min = 3, max = 50, message = "O nome deve ter entre 3 e 50 caracteres")
    private String nome;            // nome no cartao

    @NotBlank(message = "Campo número do cartão é obrigatório")
    @Size(min = 16, max = 16, message = "O número do cartão deve ter 16 caracteres")
    private String numeroCartao;    // XXXX XXXX XXXX XXXX

    @NotBlank(message = "Campo validade é requerido")
    @Size(min = 5, max = 5, message = "A validade do catão deve ter 5 caracteres")
    private String validade;        // MM/AA

    @NotBlank(message = "Campo código de segurança é requerido")
    @Size(min = 3, max = 3, message = "O código de segurança deve ter 3 caracteres")
    private String codigoSeguranca; // XXX

    private Status status;

    @NotNull(message = "Campo ID do pedido é requerido")
    private Long pedidoId;


    public PagamentoDTO(Pagamento pagamento) {
        id = pagamento.getId();
        valor = pagamento.getValor();
        nome = pagamento.getNome();
        numeroCartao = pagamento.getNumeroCartao();
        validade = pagamento.getValidade();
        codigoSeguranca = pagamento.getCodigoSeguranca();
        status = pagamento.getStatus();
        pedidoId = pagamento.getPedidoId();
    }
}
