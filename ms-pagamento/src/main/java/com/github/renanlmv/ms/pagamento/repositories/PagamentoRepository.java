package com.github.renanlmv.ms.pagamento.repositories;

import com.github.renanlmv.ms.pagamento.entities.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
}
