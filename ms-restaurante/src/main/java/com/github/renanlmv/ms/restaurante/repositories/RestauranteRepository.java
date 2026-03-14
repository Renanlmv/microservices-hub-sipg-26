package com.github.renanlmv.ms.restaurante.repositories;

import com.github.renanlmv.ms.restaurante.entities.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {
}
