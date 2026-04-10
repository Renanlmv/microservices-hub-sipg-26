package com.github.renanlmv.ms.pagamento.service;

import com.github.renanlmv.ms.pagamento.dto.PagamentoDTO;
import com.github.renanlmv.ms.pagamento.entities.Pagamento;
import com.github.renanlmv.ms.pagamento.exceptions.ResourceNotFoundException;
import com.github.renanlmv.ms.pagamento.repositories.PagamentoRepository;
import com.github.renanlmv.ms.pagamento.tests.Factory;
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
}
