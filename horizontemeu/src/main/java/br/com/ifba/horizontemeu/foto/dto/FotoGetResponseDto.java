package br.com.ifba.horizontemeu.foto.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
// DTO de SAÍDA — define quais campos são retornados ao cliente
public class FotoGetResponseDto {

    @JsonProperty(value = "url")
    private String url;

    @JsonProperty(value = "legenda")
    private String legenda;

    @JsonProperty(value = "dataUpload")
    private LocalDate dataUpload;

    @JsonProperty(value = "aprovado")
    private Boolean aprovado;
}
