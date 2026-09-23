package com.github.renanlmv.ms.pedido.service;

import com.github.renanlmv.ms.pedido.dto.ItemDoPedidoDTO;
import com.github.renanlmv.ms.pedido.dto.PedidoRequestDTO;
import com.github.renanlmv.ms.pedido.dto.PedidoResponseDTO;
import com.github.renanlmv.ms.pedido.entities.ItemDoPedido;
import com.github.renanlmv.ms.pedido.entities.Pedido;
import com.github.renanlmv.ms.pedido.entities.Status;
import com.github.renanlmv.ms.pedido.exceptions.PedidoPagoException;
import com.github.renanlmv.ms.pedido.exceptions.ResourceNotFoundException;
import com.github.renanlmv.ms.pedido.repositories.ItemDoPedidoRepository;
import com.github.renanlmv.ms.pedido.repositories.PedidoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ItemDoPedidoRepository itemDoPedidoRepository;

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> findAllPedidos() {

        return pedidoRepository.findAll().stream().map(PedidoResponseDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponseDTO findPedidoById(Long id) {

        Pedido pedido = pedidoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado. ID: " + id)
        );

        return new PedidoResponseDTO(pedido);
    }

    @Transactional
    public PedidoResponseDTO savePedido(PedidoRequestDTO requestDTO) {

        Pedido pedido = new Pedido();
        pedido.setData(LocalDate.now());
        pedido.setStatus(Status.CRIADO);
        mapDtoToPedido(requestDTO, pedido);
        pedido.calcularValorTotalDoPedido();
        pedido = pedidoRepository.save(pedido);
        return new PedidoResponseDTO(pedido);
    }

    private void mapDtoToPedido(PedidoRequestDTO requestDTO, Pedido pedido) {

        pedido.setNome(requestDTO.getNome());
        pedido.setCpf(requestDTO.getCpf());

        for (ItemDoPedidoDTO itemDTO : requestDTO.getItens()) {
            ItemDoPedido itemPedido = new ItemDoPedido();
            itemPedido.setQuantidade(itemDTO.getQuantidade());
            itemPedido.setDescricao(itemDTO.getDescricao());
            itemPedido.setPrecoUnitario(itemDTO.getPrecoUnitario());
            itemPedido.setPedido(pedido);

            pedido.getItens().add(itemPedido);
        }
    }

    @Transactional
    public PedidoResponseDTO updatePedido(Long id, PedidoRequestDTO requestDTO) {

        try {
            Pedido pedido = pedidoRepository.getReferenceById(id);

            if (pedido.getStatus().equals(Status.PAGO)) {
                throw new PedidoPagoException(
                        String.format("Pedido ID %d já está PAGO e não pode ser alterado.", id)
                );
            }

            pedido.getItens().clear();
            pedido.setData(LocalDate.now());
//            pedido.setStatus(Status.CRIADO);
            mapDtoToPedido(requestDTO, pedido);
            pedido.calcularValorTotalDoPedido();
            pedido = pedidoRepository.save(pedido);
            return new PedidoResponseDTO(pedido);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Recurso não encontrado. ID: " + id);
        }
    }

    @Transactional
    public void deletePedidoById(Long id) {

        if (!pedidoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado. ID: " + id);
        }

        pedidoRepository.deleteById(id);
    }

    @Transactional
    public void confirmarPagamento(Long id) {

        Optional<Pedido> pedido = pedidoRepository.findById(id);

        if(pedido.isEmpty()) {
            throw new ResourceNotFoundException("Pedido não encontrado. ID: " + id);
        }

        pedido.get().setStatus(Status.PAGO);
        pedidoRepository.save(pedido.get());
    }
}
