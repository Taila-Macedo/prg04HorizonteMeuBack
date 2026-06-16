package br.com.ifba.horizontemeu.roteiro.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO DE SAÍDA AUXILIAR: Serve para enviar ao front-end (React) apenas os dados que
 * a tela precisa exibir sobre um ponto dentro do roteiro, incluindo a ordem,
 * o status de visitado e o nome amigável do ponto turístico[cite: 74, 116].
 */
public class RoteiroNoPontoResponseDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("ordem")
    private Integer ordem;

    @JsonProperty("visitado")
    private Boolean visitado;

    @JsonProperty("idPontoTuristico")
    private Long idPontoTuristico;

    @JsonProperty("nomePontoTuristico")
    private String nomePontoTuristico;
}
