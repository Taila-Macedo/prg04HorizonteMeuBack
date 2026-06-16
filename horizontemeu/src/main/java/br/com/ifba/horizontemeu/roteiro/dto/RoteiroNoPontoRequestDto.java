package br.com.ifba.horizontemeu.roteiro.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO DE ENTRADA AUXILIAR: Serve para receber do front-end apenas os dados essenciais
 * de um ponto turístico na criação/edição do roteiro: o ID do ponto e a sua ordem na viagem.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoteiroNoPontoRequestDto {

    @JsonProperty(value = "idPontoTuristico")
    @NotNull(message = "O id do ponto turístico é obrigatório!")
    private Long idPontoTuristico;

    @JsonProperty(value = "ordem")
    @NotNull(message = "A ordem é obrigatória")
    private Integer ordem;
}
