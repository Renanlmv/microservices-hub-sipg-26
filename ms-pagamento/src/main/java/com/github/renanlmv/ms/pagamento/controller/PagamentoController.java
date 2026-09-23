package com.github.renanlmv.ms.pagamento.controller;

import com.github.renanlmv.ms.pagamento.dto.PagamentoRequestDTO;
import com.github.renanlmv.ms.pagamento.dto.PagamentoResponseDTO;
import com.github.renanlmv.ms.pagamento.service.PagamentoService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j  // do lombok
@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @GetMapping
    public ResponseEntity<List<PagamentoResponseDTO>> getAllPagamentos() {

        List<PagamentoResponseDTO> list = pagamentoService.findAllPagamentos();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagamentoResponseDTO> getPagamentoById(@PathVariable Long id) {

        PagamentoResponseDTO pagamentoResponseDTO = pagamentoService.findPagamentoById(id);
        return ResponseEntity.ok(pagamentoResponseDTO);
    }

    @PostMapping
    public ResponseEntity<PagamentoResponseDTO> createPagamento(@RequestBody @Valid PagamentoRequestDTO pagamentoRequestDTO) {

        PagamentoResponseDTO pagamentoResponseDTO = pagamentoService.savePagamento(pagamentoRequestDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(pagamentoResponseDTO.getId())
                .toUri();

        return ResponseEntity.created(uri).body(pagamentoResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagamentoResponseDTO> updateProduto(@PathVariable Long id, @RequestBody @Valid PagamentoRequestDTO pagamentoRequestDTO) {

        PagamentoResponseDTO pagamentoResponseDTO = pagamentoService.updatePagamento(id, pagamentoRequestDTO);

        return ResponseEntity.ok(pagamentoResponseDTO);
    }

    @PatchMapping("/{id}/confirmar")
    @CircuitBreaker(name = "atualizarPedido", fallbackMethod = "fallbackConfirmarPagamentoPendente")
    public ResponseEntity<PagamentoResponseDTO> confirmarPagamentoDoPedido(@PathVariable @NotNull Long id) {

        PagamentoResponseDTO pagamentoResponseDTO = pagamentoService.confirmarPagamentoDoPedido(id);

        return ResponseEntity.ok(pagamentoResponseDTO);
    }

    // metodo com a mesma assinatura e tipo de retorno de cofnirmarPagamentoDoPedido
    public ResponseEntity<PagamentoResponseDTO> fallbackConfirmarPagamentoPendente(Long id, Throwable e) {
        // Registra o erro para fins de log/observabilidade
        log.error("Falha ao confirmar pedido {}. Ativando fallback. Erro: {}", id, e.getMessage());
        PagamentoResponseDTO dto = pagamentoService.alterarStatusDoPagamento(id);
        // 503 - explicitar que o serviço destino falhou, mas ainda assim enviando o corpo.
        return ResponseEntity.status(503).body(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePagamento(@PathVariable Long id) {

        pagamentoService.deletePagamentoById(id);

        return ResponseEntity.noContent().build();
    }
}
