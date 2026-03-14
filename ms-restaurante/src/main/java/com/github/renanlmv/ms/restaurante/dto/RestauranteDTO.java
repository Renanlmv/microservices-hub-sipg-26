package com.github.renanlmv.ms.restaurante.dto;

import com.github.renanlmv.ms.restaurante.entities.Restaurante;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RestauranteDTO {

    // a validação é feita no DTO
    private Long id;

    @NotBlank(message = "Nome é requerido")
    @Size(min = 5, max = 120, message = "O nome do restaurante deve ter de 5 a 120 caracteres")
    private String nome;

    @NotBlank(message = "Endereço é requerido")
    @Size(min = 5, max = 120, message = "O endereço do restaurante deve ter de 5 a 120 caracteres")
    private String endereco;

    @NotBlank(message = "Cidade é requerido")
    @Size(min = 3, max = 120, message = "A cidade em que fica o restaurante deve ter de 3 a 120 caracteres")
    private String cidade;

    @NotBlank(message = "Unidade Federativa é requerido")
    private String uf;

    public RestauranteDTO(Restaurante restaurante) {
        id = restaurante.getId();
        nome = restaurante.getNome();
        endereco = restaurante.getEndereco();
        cidade = restaurante.getCidade();
        uf = restaurante.getUf();
    }
}
