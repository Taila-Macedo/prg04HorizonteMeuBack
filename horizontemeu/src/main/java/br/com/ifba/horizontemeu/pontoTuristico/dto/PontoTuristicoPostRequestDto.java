package br.com.ifba.horizontemeu.pontoTuristico.dto;

import br.com.ifba.horizontemeu.pontoTuristico.enums.Categoria;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// DTO de ENTRADA — define quais campos o cliente pode enviar
public class PontoTuristicoPostRequestDto {

    //nome do ponto
    @JsonProperty(value = "nome")
    @NotNull(message = "O nome é obrigatório!")
    @NotBlank(message = "O nome não pode ser vazio!")
    private String nome;

    // campo opcional, sem validação
    @JsonProperty(value = "descricao")
    private String descricao;

    @JsonProperty(value = "cidade")
    @NotNull(message = "A cidade é obrigatória!")
    @NotBlank(message = "A cidade não pode ser vazia!")
    private String cidade;

    @JsonProperty(value = "pais")
    @NotNull(message = "O país é obrigatório!")
    @NotBlank(message = "O país não pode ser vazio!")
    private String pais;

    @JsonProperty(value = "latitude")
    @NotNull(message = "A latitude é obrigatória!")
    private Float latitude;

    @JsonProperty(value = "longitude")
    @NotNull(message = "A longitude é obrigatória!")
    private Float longitude;

    @JsonProperty(value = "categoria")
    @NotNull(message = "A categoria é obrigatória!")
    private Categoria categoria;

    // NOVO: opcional — se não vier, o service assume false
    @JsonProperty(value = "noMapa3D")
    private Boolean noMapa3D;
}
