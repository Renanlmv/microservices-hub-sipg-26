package com.github.renanlmv.ms.pagamento.service;

import com.github.renanlmv.ms.pagamento.client.PedidoClient;
import com.github.renanlmv.ms.pagamento.dto.PagamentoDTO;
import com.github.renanlmv.ms.pagamento.entities.Pagamento;
import com.github.renanlmv.ms.pagamento.entities.Status;
import com.github.renanlmv.ms.pagamento.exceptions.PagamentoAprovadoException;
import com.github.renanlmv.ms.pagamento.exceptions.ResourceNotFoundException;
import com.github.renanlmv.ms.pagamento.repositories.PagamentoRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private PedidoClient pedidoClient;

    @Transactional(readOnly = true)
    public List<PagamentoDTO> findAllPagamentos() {

        return pagamentoRepository.findAll().stream().map(PagamentoDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public PagamentoDTO findPagamentoById(Long id) {

        Pagamento pagamento = pagamentoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado. ID: " + id)
        );

        return new PagamentoDTO(pagamento);
    }

    @Transactional
    public PagamentoDTO savePagamento(PagamentoDTO pagamentoDTO) {

        Pagamento pagamento = new Pagamento();
        mapDtoToPagamento(pagamentoDTO, pagamento);
        pagamento.setStatus(Status.CRIADO);
        pagamento = pagamentoRepository.save(pagamento);
        return new PagamentoDTO(pagamento);
    }

    @Transactional
    public PagamentoDTO confirmarPagamentoDoPedido(Long id) {

        Pagamento pagamento = pagamentoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Pagamento não encontrado. ID: " + id)
        );

        pagamento.setStatus(Status.APROVADO);
        pagamentoRepository.save(pagamento);

        try {
            pedidoClient.confirmarPagamento(pagamento.getPedidoId());
        } catch (FeignException.NotFound e) { // 404 do ms-pedido
            // não existe pedido para receber a confirmação
            throw new ResourceNotFoundException("Pedido não encontrado. ID: " + pagamento.getPedidoId());
        } catch (FeignException e) {
            // outros erros (400/500/timeout etc.)
            throw new RuntimeException("Falha ao comunicar com ms-pedido.", e);
        }

        return new PagamentoDTO(pagamento);
    }

    private void mapDtoToPagamento(PagamentoDTO pagamentoDTO, Pagamento pagamento) {

        pagamento.setNome(pagamentoDTO.getNome());
        pagamento.setValor(pagamentoDTO.getValor());
        pagamento.setValidade(pagamentoDTO.getValidade());
        pagamento.setCodigoSeguranca(pagamentoDTO.getCodigoSeguranca());
        pagamento.setNumeroCartao(pagamentoDTO.getNumeroCartao());
        pagamento.setPedidoId(pagamentoDTO.getPedidoId());
    }

    @Transactional
    public PagamentoDTO alterarStatusDoPagamento(Long id) {

        Pagamento pagamento = pagamentoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Pagamento não encontrado. ID: " + id)
        );

        pagamento.setStatus(Status.CONFIRMACAO_PENDENTE);
        pagamento = pagamentoRepository.save(pagamento);
        return new PagamentoDTO(pagamento);
    }

    @Transactional
    public PagamentoDTO updatePagamento(Long id, PagamentoDTO pagamentoDTO) {

        try {
            Pagamento pagamento = pagamentoRepository.getReferenceById(id);

            if(pagamento.getStatus().equals(Status.APROVADO)) {
                throw new PagamentoAprovadoException(
                        String.format("Pagamento ID %d já está APROVADO e não pode ser alterado.", id)
                );
            }

            mapDtoToPagamento(pagamentoDTO, pagamento);
            pagamento.setStatus(pagamentoDTO.getStatus());
            pagamento = pagamentoRepository.save(pagamento);
            return new PagamentoDTO(pagamento);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Recurso não encontrado. ID: " + id);
        }
    }

    @Transactional
    public void deletePagamentoById(Long id) {

        if(!pagamentoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encntrado. ID: " + id);
        }
        pagamentoRepository.deleteById(id);
    }
}
