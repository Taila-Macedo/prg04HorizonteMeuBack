package br.com.ifba.horizontemeu.pontoTuristico.dto;

import br.com.ifba.horizontemeu.pontoTuristico.enums.Categoria;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// DTO de SAÍDA — define quais campos são retornados ao cliente
public class PontoTuristicoGetResponseDto {

    @JsonProperty(value = "nome")
    private String  nome;

    @JsonProperty(value = "descricao")
    private String descricao;

    @JsonProperty(value = "cidade")
    private String cidade;

    @JsonProperty(value = "pais")
    private String pais;

    @JsonProperty(value = "latitude")
    private Float latitude;

    @JsonProperty(value = "longitude")
    private Float longitude;

    @JsonProperty(value = "notaMedia")
    private Float notaMedia;

    @JsonProperty(value = "categoria")
    private Categoria categoria;
}
