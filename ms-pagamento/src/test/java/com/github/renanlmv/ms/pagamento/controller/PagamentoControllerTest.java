package com.github.renanlmv.ms.pagamento.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.renanlmv.ms.pagamento.dto.PagamentoDTO;
import com.github.renanlmv.ms.pagamento.entities.Pagamento;
import com.github.renanlmv.ms.pagamento.exceptions.ResourceNotFoundException;
import com.github.renanlmv.ms.pagamento.service.PagamentoService;
import com.github.renanlmv.ms.pagamento.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

@WebMvcTest(PagamentoController.class)
public class PagamentoControllerTest {

    @Autowired
    private MockMvc mockMvc; // chamar os endpoints

    @Autowired
    // converte para JSON objeto Java e envia na requisição
    private ObjectMapper objectMapper;

    @MockitoBean
    private PagamentoService pagamentoService;
    private Pagamento pagamento;
    private Long existingId;
    private Long nonExistingId;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = Long.MAX_VALUE;
        pagamento = Factory.createPagamento();
    }

    @Test
    @DisplayName("findAllPagamentos deveria retornar uma lista de PagamentoDTO")
    void findAllPagamentosShouldReturnListPagamentoDTO() throws Exception {

        // Arrange
        PagamentoDTO inputDto = new PagamentoDTO(pagamento);
        List<PagamentoDTO> list = List.of(inputDto);
        Mockito.when(pagamentoService.findAllPagamentos()).thenReturn(list);

        // Act + Assert
        ResultActions result = mockMvc.perform(get("/pagamentos")
                .accept(MediaType.APPLICATION_JSON) );// request: Accept
        result.andDo(print());
        result.andExpect(status().isOk());
        result.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        result.andExpect(jsonPath("$").isArray());
        result.andExpect(jsonPath("$[0].id").value(pagamento.getId()));
        result.andExpect(jsonPath("$[0].valor").value(pagamento.getValor().doubleValue()));

        // Verify (comportamento)
        Mockito.verify(pagamentoService).findAllPagamentos();
        Mockito.verifyNoMoreInteractions(pagamentoService);
    }

    @Test
    @DisplayName("findPagamentoById deveria retornar PagamentoDTO quando o Id existir")
    void findPagamentoByIdShouldReturnPagamentoDTOWhenIdWxists() throws Exception {

        PagamentoDTO responseDTO = new PagamentoDTO(pagamento);
        Mockito.when(pagamentoService.findPagamentoById(existingId)).thenReturn(responseDTO);

        mockMvc.perform(get("/pagamentos/{id}", existingId)
                .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(existingId))
                .andExpect(jsonPath("$.valor").value(pagamento.getValor().doubleValue()))
                .andExpect(jsonPath("$.status").value(pagamento.getStatus().name()))
                .andExpect(jsonPath("$.pedidoId").value(pagamento.getPedidoId()));

        Mockito.verify(pagamentoService).findPagamentoById(existingId);
        Mockito.verifyNoMoreInteractions(pagamentoService);
    }

    @Test
    @DisplayName("findPagamentoById deveria retornar 404 quando o Id não existir")
    void findPagamentoByIdShouldReturn404WhenIdDoesNotExist() throws Exception {

        Mockito.when(pagamentoService.findPagamentoById(nonExistingId))
                .thenThrow(new ResourceNotFoundException("Recurso não encontrado. ID: " + nonExistingId));

        mockMvc.perform(get("/pagamentos/{id}", nonExistingId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());

        Mockito.verify(pagamentoService).findPagamentoById(nonExistingId);
        Mockito.verifyNoMoreInteractions(pagamentoService);
    }

    @Test
    @DisplayName("createPagamento deveria retornar 201 quando for válido")
    void createPagamentoShouldReturn201WhenValid() throws Exception {

        PagamentoDTO requestDTO = new PagamentoDTO(Factory.createPagamentoSemId());
        // Bean objectMapper para converter JAVA para JSON
        String jsonRequestBody = objectMapper.writeValueAsString(requestDTO);
        PagamentoDTO responseDTO = new PagamentoDTO(pagamento);
        Mockito.when(pagamentoService.savePagamento(any(PagamentoDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/pagamentos")
                .contentType(MediaType.APPLICATION_JSON) // request Content_Type
                .accept(MediaType.APPLICATION_JSON) // request Accept
                .content(jsonRequestBody)) // request body
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)) // response
                .andExpect(jsonPath("$.id").value(pagamento.getId()))
                .andExpect(jsonPath("$.status").value(pagamento.getStatus().name()))
                .andExpect(jsonPath("$.valor").value(pagamento.getValor().doubleValue()))
                .andExpect(jsonPath("$.pedidoId").value(pagamento.getPedidoId()));

        Mockito.verify(pagamentoService).savePagamento(any(PagamentoDTO.class));
        Mockito.verifyNoMoreInteractions(pagamentoService);
    }

    @Test
    @DisplayName("createPagamento deveria retornar 422 quando for inválido")
    void createPagamentoShouldReturn422WhenInvalid() throws Exception {

        Pagamento pagamentoInvalido = Factory.createPagamentoSemId();
        pagamentoInvalido.setValor(BigDecimal.valueOf(0));
        pagamentoInvalido.setNome(null);
        PagamentoDTO requestDTO = new PagamentoDTO(pagamentoInvalido);
        String jsonRequestBody = objectMapper.writeValueAsString(requestDTO);
        PagamentoDTO responseDTO = new PagamentoDTO(pagamentoInvalido);

        Mockito.when(pagamentoService.savePagamento(any(PagamentoDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/pagamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

        Mockito.verifyNoInteractions(pagamentoService);
    }

    @Test
    @DisplayName("updatePagamento deveria retornar 200 quando for válido")
    void updatePagamentoShouldReturn200WhenValid() throws Exception {

        PagamentoDTO requestDTO = new PagamentoDTO(Factory.createPagamento());
        String jsonRequestBody = objectMapper.writeValueAsString(requestDTO);
        PagamentoDTO responseDTO = new PagamentoDTO(pagamento);
        Mockito.when(pagamentoService.updatePagamento(eq(existingId), any(PagamentoDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(put("/pagamentos/{id}", existingId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(existingId))
                .andExpect(jsonPath("$.status").value(pagamento.getStatus().name()))
                .andExpect(jsonPath("$.pedidoId").value(pagamento.getPedidoId()));

        Mockito.verify(pagamentoService).updatePagamento(eq(existingId), any(PagamentoDTO.class));
        Mockito.verifyNoMoreInteractions(pagamentoService);
    }

    @Test
    @DisplayName("deletePagamento deveria retornar 204 quando o Id existir")
    void deletePagamentoShouldReturn204WhenIdExists() throws Exception {

        Mockito.doNothing().when(pagamentoService).deletePagamentoById(existingId);

        mockMvc.perform(delete("/pagamentos/{id}", existingId))
                .andExpect(status().isNoContent());

        Mockito.verify(pagamentoService).deletePagamentoById(existingId);
        Mockito.verifyNoMoreInteractions(pagamentoService);
    }

    @Test
    @DisplayName("deletePagamento deveria retornar 404 quando o Id não existir")
    void deletePagamentoShouldReturn404WhenIdDoesNotExist() throws Exception {

        Mockito.doThrow(new ResourceNotFoundException("Recurso não encontrado. ID: " + nonExistingId))
                .when(pagamentoService).deletePagamentoById(nonExistingId);

        mockMvc.perform(delete("/pagamentos/{id}", nonExistingId))
                .andExpect(status().isNotFound());

        Mockito.verify(pagamentoService).deletePagamentoById(nonExistingId);
        Mockito.verifyNoMoreInteractions(pagamentoService);
    }
}
