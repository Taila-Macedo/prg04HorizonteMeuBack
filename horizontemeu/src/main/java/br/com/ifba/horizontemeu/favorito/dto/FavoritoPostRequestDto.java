package br.com.ifba.horizontemeu.favorito.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FavoritoPostRequestDto {

    @JsonProperty(value = "idUsuario")
    @NotNull(message = "O id usuário é obrigatório!")
    private Long idUsuario;

    @JsonProperty(value = "idPontoTuristico")
    @NotNull(message = "O id ponto turístico é obrigatório!")
    private Long idPontoTuristico;
}
