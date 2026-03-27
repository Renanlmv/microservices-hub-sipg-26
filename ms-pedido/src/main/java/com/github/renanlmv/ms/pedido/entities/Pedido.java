package com.github.renanlmv.ms.pedido.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")

@Entity
@Table(name = "tb_pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column define caradterísticas da coluna no DB
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    // @Column(unique = true, nullable = false, length = 11) -> unique para permitir apenas um cpf
    @Column(nullable = false, length = 11)
    private String cpf;

    private LocalDate data;

    @Enumerated(EnumType.STRING)
    private Status status;

    // valor calculado
    @Column(name = "valor_total")
    private BigDecimal valorTotal;

    // relacionamento
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemDoPedido> itens = new ArrayList<>();

    public void calcularValorTotalDoPedido() {
        this.valorTotal = this.itens.stream()
                .map(i -> i.getPrecoUnitario()
                        .multiply(BigDecimal.valueOf(i.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
