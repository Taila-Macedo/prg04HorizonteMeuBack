package br.com.ifba.horizontemeu.foto.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
// DTO de ENTRADA — define quais campos o cliente pode enviar
public class FotoPostRequestDto {

    @JsonProperty(value = "url")
    @NotNull(message = "A url é obrigatória!")
    @NotBlank(message = "A url não pode ser vazia!")
    private String url;

    @JsonProperty(value = "legenda")
    private String legenda;

    @JsonProperty(value = "idUsuario")
    @NotNull(message = "O id do usuario é obrigatório!")
    private Long idUsuario;

    @JsonProperty(value = "idPontoTuristico")
    @NotNull(message = "O id do ponto turístico é obrigatório!")
    private Long idPontoTuristico;
}
