package br.com.ifba.horizontemeu.favorito.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO de SAÍDA — define quais campos são retornados ao cliente.
 * O idUsuario não é retornado pois o cliente já o informou na requisição.
 * O idPontoTuristico é incluído porque o frontend precisa saber
 * QUAIS pontos turísticos foram favoritados pelo usuário,
 * para poder exibir o status de "favoritado" na lista de pontos.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoritoGetResponseDto {

    @JsonProperty(value = "dataSalvo")
    private LocalDate dataSalvo;

    @JsonProperty(value = "idPontoTuristico")
    private Long idPontoTuristico;
}
