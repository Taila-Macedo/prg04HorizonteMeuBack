package br.com.ifba.horizontemeu.roteiro.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// DTO de SAÍDA — define quais campos são retornados ao cliente
public class RoteiroGetResponseDto {

    @JsonProperty(value = "id")
    private Long id;

    @JsonProperty(value = "titulo")
    private String titulo;

    @JsonProperty(value = "descricao")
    private String descricao;

    @JsonProperty(value = "dataViagem")
    private LocalDate dataViagem;

    @JsonProperty(value = "dataCriacao")
    private LocalDate dataCriacao;

    @JsonProperty(value = "idUsuario")
    private Long idUsuario;

    @JsonProperty("pontos")
    private List<RoteiroNoPontoResponseDto> pontos;
}
