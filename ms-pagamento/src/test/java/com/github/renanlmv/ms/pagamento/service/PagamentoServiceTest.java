package com.github.renanlmv.ms.pagamento.service;

import com.github.renanlmv.ms.pagamento.dto.PagamentoDTO;
import com.github.renanlmv.ms.pagamento.entities.Pagamento;
import com.github.renanlmv.ms.pagamento.exceptions.ResourceNotFoundException;
import com.github.renanlmv.ms.pagamento.repositories.PagamentoRepository;
import com.github.renanlmv.ms.pagamento.tests.Factory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class) // nao sobe o contexto do Spring
public class PagamentoServiceTest {

    // Cenario: A SUT (classe testada) depende do PagamentoRepository

    @Mock // cria um duble (mock) do repository para isolar o teste
    private PagamentoRepository pagamentoRepository;

    @InjectMocks // cria a instancia real do service (SUT) e injeta os mocks nela
    private PagamentoService pagamentoService;

    // Nao acessa DB | Preparando os dados - variaveis
    private Long existingId;
    private Long nonExistingId;

    private Pagamento pagamento;

    // executado antes de cada teste
    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = Long.MAX_VALUE;

        pagamento = Factory.createPagamento();
    }

    @Test
    void deletePagamentoByIdShouldDeleteWhenIdExists() {
        // Arrange - prepara o comportamento do mock (stubbing)
        Mockito.when(pagamentoRepository.existsById(existingId)).thenReturn(true);

        pagamentoService.deletePagamentoById(existingId);

        // verify(...) = verifica se o mock foi chamado
        // Verifica que o mock pagamentoRepository recebeu uma chamada ao metodo existsById
        Mockito.verify(pagamentoRepository).existsById(existingId);
        // Verifica se o metodo deleteById do repository foi chamado exatamente 1 vez (padrao)
        Mockito.verify(pagamentoRepository, Mockito.times(1)).deleteById(existingId);
    }

    @Test
    @DisplayName("deletePagamentoBydId deveria lançar ResourceNotFoundException quando o Id não existir")
    void deletePagamentoByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        // Arrange
        Mockito.when(pagamentoRepository.existsById(nonExistingId)).thenReturn(false);
        // Act + Assert
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> {
                    pagamentoService.deletePagamentoById(nonExistingId);
                });
        // Verificacoes (behavior)
        Mockito.verify(pagamentoRepository).existsById(nonExistingId);
        // never() = equivalente a times(0) -> esse metodo nao pode ter sido chamado nenhuma vez
        // anyLong() é um matcher (coringa): aceita qualquer valor long/long
        Mockito.verify(pagamentoRepository, Mockito.never()).deleteById(Mockito.anyLong());
    }

    @Test
    void findPagamentoByIdShouldReturnPagamentoDTOWhenIdExists() {

        Mockito.when(pagamentoRepository.findById(existingId))
                .thenReturn(Optional.of(pagamento));

        PagamentoDTO restul = pagamentoService.findPagamentoById(existingId);

        Assertions.assertNotNull(pagamento);
        Assertions.assertEquals(pagamento.getId(), restul.getId());
        Assertions.assertEquals(pagamento.getValor(), restul.getValor());

        Mockito.verify(pagamentoRepository).findById(existingId);
        Mockito.verifyNoMoreInteractions(pagamentoRepository);

    }

    @Test
    void findPagamentoByIdShoudThrowResourceNotFoundExceptionWhenIdNotExist() {

        Mockito.when(pagamentoRepository.findById(nonExistingId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> pagamentoService.findPagamentoById(nonExistingId));

        Mockito.verify(pagamentoRepository).findById(nonExistingId);
        Mockito.verifyNoMoreInteractions(pagamentoRepository);
    }

    @Test
    void givenValidParamsAndIdIsNull_whenSave_thenShouldPersistPagamento() {

        Mockito.when(pagamentoRepository.save(any(Pagamento.class)))
                .thenReturn(pagamento);
        pagamento.setId(null);
        PagamentoDTO inputDTO = new PagamentoDTO(pagamento);

        PagamentoDTO result = pagamentoService.savePagamento(inputDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(pagamento.getId(), result.getId());

        Mockito.verify(pagamentoRepository).save(any(Pagamento.class));
        Mockito.verifyNoMoreInteractions(pagamentoRepository);
    }

    @Test
    void updatePagamentoShouldReturnPagamentoDTOWhenIdExists() {

        Long id = pagamento.getId();
        Mockito.when(pagamentoRepository.getReferenceById(existingId)).thenReturn(pagamento);
        Mockito.when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamento);

        PagamentoDTO result = pagamentoService.updatePagamento(id, new PagamentoDTO(pagamento));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(pagamento.getId(), result.getId());
        Assertions.assertEquals(pagamento.getValor(), result.getValor());

        Mockito.verify(pagamentoRepository).getReferenceById(existingId);
        Mockito.verify(pagamentoRepository).save(any(Pagamento.class));

        Mockito.verifyNoMoreInteractions(pagamentoRepository);


    }

    @Test
    void updatePagamentoShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Mockito.when(pagamentoRepository.getReferenceById(nonExistingId))
                .thenThrow(EntityNotFoundException.class);

        PagamentoDTO inputDTO = new PagamentoDTO(pagamento);

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> pagamentoService.updatePagamento(nonExistingId, inputDTO)
        );

        Mockito.verify(pagamentoRepository).getReferenceById(nonExistingId);
        Mockito.verify(pagamentoRepository, Mockito.never()).save(Mockito.any(Pagamento.class));
        Mockito.verifyNoMoreInteractions(pagamentoRepository);
    }
}
