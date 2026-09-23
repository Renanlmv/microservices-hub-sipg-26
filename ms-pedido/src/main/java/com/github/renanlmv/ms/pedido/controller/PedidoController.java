package com.github.renanlmv.ms.pedido.controller;

import com.github.renanlmv.ms.pedido.dto.PedidoRequestDTO;
import com.github.renanlmv.ms.pedido.dto.PedidoResponseDTO;
import com.github.renanlmv.ms.pedido.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    // Testando load balancing: devolve a porta da instância
    @GetMapping("/port")
    public String port(@Value("${local.server.port}") String porta) {
        return "Instância respondeu na porta " + porta;
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> getAllPedidos() {

        List<PedidoResponseDTO> list = pedidoService.findAllPedidos();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> getPedidoById(@PathVariable Long id) {

        PedidoResponseDTO responseDTO = pedidoService.findPedidoById(id);

        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> createPedido(@RequestBody @Valid PedidoRequestDTO requestDTO) {

         PedidoResponseDTO responseDTO = pedidoService.savePedido(requestDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(responseDTO.getId())
                .toUri();
        return ResponseEntity.created(uri).body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> updatePedido(@PathVariable Long id, @RequestBody @Valid PedidoRequestDTO requestDTO) {

        PedidoResponseDTO responseDTO = pedidoService.updatePedido(id, requestDTO);

        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{pedidoId}/pagamento/confirmado")
    public void confirmarPagamento(@PathVariable Long pedidoId) {

        pedidoService.confirmarPagamento(pedidoId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable Long id) {

        pedidoService.deletePedidoById(id);

        return ResponseEntity.noContent().build();
    }


}
