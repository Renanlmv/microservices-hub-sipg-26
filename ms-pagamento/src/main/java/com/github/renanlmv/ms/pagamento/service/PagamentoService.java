package com.github.renanlmv.ms.pagamento.service;

import com.github.renanlmv.ms.pagamento.dto.PagamentoDTO;
import com.github.renanlmv.ms.pagamento.entities.Pagamento;
import com.github.renanlmv.ms.pagamento.entities.Status;
import com.github.renanlmv.ms.pagamento.exceptions.ResourceNotFoundException;
import com.github.renanlmv.ms.pagamento.repositories.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

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

    private void mapDtoToPagamento(PagamentoDTO pagamentoDTO, Pagamento pagamento) {

        pagamento.setNome(pagamentoDTO.getNome());
        pagamento.setValor(pagamentoDTO.getValor());
        pagamento.setValidade(pagamentoDTO.getValidade());
        pagamento.setCodigoSeguranca(pagamentoDTO.getCodigoSeguranca());
        pagamento.setNumeroCartao(pagamentoDTO.getNumeroCartao());
        pagamento.setPedidoId(pagamentoDTO.getPedidoId());
    }

    @Transactional
    public PagamentoDTO updatePagamento(Long id, PagamentoDTO pagamentoDTO) {

        try {
            Pagamento pagamento = pagamentoRepository.getReferenceById(id);
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
