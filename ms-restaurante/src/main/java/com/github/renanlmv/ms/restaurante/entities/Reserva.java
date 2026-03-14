package com.github.renanlmv.ms.restaurante.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_reserva")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate dataReserva;
    private String nomeCliente;
    private Integer qtdePessoas;

    // relacionamento
    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;
}
