package br.com.ifba.horizontemeu.comentario.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComentarioPostRequestDto {

    @JsonProperty(value = "texto")
    @NotNull(message = "O texto do Comentário é obrigatório!")
    @NotBlank(message = "O texto do comentário não pode ser vazio!")
    private String texto;

    @JsonProperty(value = "nota")
    @NotNull(message = "A nota é obrigatória!")
    private Integer nota;

    @JsonProperty(value = "fotoUrl")
    private String fotoUrl;

    @JsonProperty(value = "idUsuario")
    @NotNull(message = "O id do usuário é obrigatório!")
    private Long idUsuario;

    @JsonProperty(value = "idPontoTuristico")
    @NotNull(message = "O id do ponto turístico é obrigatório!")
    private Long idPontoTuristico;

}
