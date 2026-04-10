package com.github.renanlmv.ms.pagamento.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "tb_pagamento")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    private String nome;            // nome no cartao

    @Column(nullable = false)
    private String numeroCartao;    // XXXX XXXX XXXX XXXX

    @Column(nullable = false)
    private String validade;        // MM/AA

    @Column(nullable = false)
    private String codigoSeguranca; // XXX

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private Long pedidoId;
}
