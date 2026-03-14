package com.github.renanlmv.ms.restaurante.dto;

import com.github.renanlmv.ms.restaurante.entities.Reserva;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ReservaDTO {

    // validação é feita no DTO
    private Long id;

    @NotNull(message = "Data da Reserva é requerido")
    @FutureOrPresent(message = "Data da reserva deve ser atual ou futura")
    private LocalDate dataReserva;

    @NotBlank(message = "Nome do Cliente é requerido")
    @Size(min = 5, max = 100, message = "O nome do cliente deve ter de 5 a 100 caracteres")
    private String nomeCliente;

    @NotNull(message = "Quantidade de pessoas é requerido")
    @Positive(message = "Quantidade de pessoas deve ser um número inteiro maior que zero")
    private Integer qtdePessoas;

    @NotNull(message = "Restaurante é obrigatório")
    private RestauranteDTO restaurante;

//    public ReservaDTO (Reserva reserva) {
//        id = reserva.getId();
//        dataReserva = reserva.getDataReserva();
//        nomeCliente = reserva.getNomeCliente();
//        qtdePessoas = reserva.getQtdePessoas();
//        restaurante = reserva.getRestaurante();
//    }
}
