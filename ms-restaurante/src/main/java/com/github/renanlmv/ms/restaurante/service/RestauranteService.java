package com.github.renanlmv.ms.restaurante.service;

import com.github.renanlmv.ms.restaurante.dto.RestauranteDTO;
import com.github.renanlmv.ms.restaurante.entities.Restaurante;
import com.github.renanlmv.ms.restaurante.exceptions.ResourceNotFoundException;
import com.github.renanlmv.ms.restaurante.repositories.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RestauranteService {

    @Autowired
    RestauranteRepository restauranteRepository;

    // transactional para fazer as transações serem atômicas (ou ela é executada por completo ou não é executada)
    // readOnly para indicar que esse metodo vai apenas fazer consultas ao banco de dados -
    // - evita verificações desnecessárias
    @Transactional(readOnly = true)
    public List<RestauranteDTO> findAllRestaurantes() {

        return restauranteRepository.findAll().stream().map(RestauranteDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public RestauranteDTO findRestauranteById(Long id) {

        // se no erro aparecer que o "Provided" é Optional, é porque não é garantido que o resultado -
        // - vai vir como o esperado, então tem que colocar um else (orElseThrow)
        Restaurante restaurante = restauranteRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado. ID: " + id)
        );

        return new RestauranteDTO(restaurante);
    }

    @Transactional
    public RestauranteDTO saveRestaurante (RestauranteDTO restauranteDTO) {

        Restaurante restaurante = new Restaurante();
        mapDtoToRestaurante(restaurante, restauranteDTO);
        restaurante = restauranteRepository.save(restaurante);

        return new RestauranteDTO(restaurante);
    }

    public void mapDtoToRestaurante (Restaurante restaurante, RestauranteDTO restauranteDTO) {

        restaurante.setNome(restauranteDTO.getNome());
        restaurante.setEndereco(restauranteDTO.getEndereco());
        restaurante.setCidade(restauranteDTO.getCidade());
        restaurante.setUf(restauranteDTO.getUf());
    }

    @Transactional
    public RestauranteDTO updateRestaurante (Long id, RestauranteDTO restauranteDTO) {

        Restaurante restaurante = restauranteRepository.getReferenceById(id);
        mapDtoToRestaurante(restaurante, restauranteDTO);
        restaurante = restauranteRepository.save(restaurante);

        return new RestauranteDTO(restaurante);
    }

    @Transactional
    public void deleteRestaurante (Long id) {

        // verifica se o restaurante não existe
        // Se não existir, lança exceção
        if (!restauranteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado. ID: " + id);
        }

        restauranteRepository.deleteById(id);
    }

}
