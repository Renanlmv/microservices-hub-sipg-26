package com.github.renanlmv.ms.restaurante.controller;

import com.github.renanlmv.ms.restaurante.dto.RestauranteDTO;
import com.github.renanlmv.ms.restaurante.service.ReservaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    ReservaService reservaService;


}
