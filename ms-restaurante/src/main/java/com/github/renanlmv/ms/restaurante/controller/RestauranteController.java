package com.github.renanlmv.ms.restaurante.controller;

import com.github.renanlmv.ms.restaurante.dto.RestauranteDTO;
import com.github.renanlmv.ms.restaurante.service.RestauranteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/restaurantes")
public class RestauranteController {

    // autowired pra injetar as dependências certas (não precisar fazer o new)
    @Autowired
    RestauranteService restauranteService;

    // getmapping para mapear que esse metodo é get
    @GetMapping
    public ResponseEntity<List<RestauranteDTO>> getAllRestaurantes() {

        // gera uma lista para receber os restaurantes encontrados
        List<RestauranteDTO> list = restauranteService.findAllRestaurantes();

        // ok retorna status 200(OK) no Insomnia
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestauranteDTO> getRestauranteById(@PathVariable Long id) {

        RestauranteDTO restauranteDTO = restauranteService.findRestauranteById(id);
        return ResponseEntity.ok(restauranteDTO);
    }

    // postmapping para mapear que esse metodo é post
    @PostMapping
    public ResponseEntity<RestauranteDTO> createRestaurante (@RequestBody RestauranteDTO restauranteDTO) {

        restauranteDTO = restauranteService.saveRestaurante(restauranteDTO);

        // para aparecer na uri o local que o elemento foi inserido
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id")
                .buildAndExpand(restauranteDTO.getId())
                .toUri();

        // created retorna status 201(Created) no Insomnia
        return ResponseEntity.created(uri).body(restauranteDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestauranteDTO> updateRestaurante (@PathVariable Long id,
                                                             @RequestBody RestauranteDTO restauranteDTO) {

        restauranteDTO = restauranteService.updateRestaurante(id, restauranteDTO);
        return ResponseEntity.ok(restauranteDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurante (@PathVariable Long id) {

        restauranteService.deleteRestaurante(id);

        // noContent retorna status 204(No Content) no Insomnia
        return ResponseEntity.noContent().build();
    }
}
