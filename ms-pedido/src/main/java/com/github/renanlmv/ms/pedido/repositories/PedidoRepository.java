package com.github.renanlmv.ms.pedido.repositories;

import com.github.renanlmv.ms.pedido.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
