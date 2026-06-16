package br.com.ifba.horizontemeu.roteiro.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
// DTO de ENTRADA — define quais campos o cliente pode enviar
public class RoteiroPostRequestDto {

    @JsonProperty(value = "titulo")
    @NotNull(message = "O título é obrigatório!")
    @NotBlank(message = "O título não pode ser vazio!")
    private String titulo;

    @JsonProperty(value = "descricao")
    private String descricao;

    @JsonProperty(value = "dataViagem")
    private LocalDate dataViagem;

    @JsonProperty(value = "idUsuario")
    @NotNull(message = "O id do usuario é obrigatório!")
    private Long idUsuario;

    @JsonProperty(value = "publico")
    private Boolean publico = false;

    // Lista de pontos da viagem. O @Valid garante a validação do ID e da ordem de cada ponto.
    // É opcional: permite criar o roteiro já com locais ou totalmente vazio.
    @Valid
    private List<RoteiroNoPontoRequestDto> pontos;
}
