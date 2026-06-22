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

/**
 * DTO de ENTRADA para atualização de roteiro (PUT /roteiros/{id}).
 *
 * Separado do RoteiroPostRequestDto intencionalmente:
 *   - idUsuario não muda — o roteiro pertence ao mesmo usuário para sempre
 *   - A lista de pontos pode ser reenviada completa para substituição
 */
@Getter
@Setter
@NoArgsConstructor
public class RoteiroPutRequestDto {

    @JsonProperty("titulo")
    @NotNull(message = "O título é obrigatório!")
    @NotBlank(message = "O título não pode ser vazio!")
    private String titulo;

    @JsonProperty("descricao")
    private String descricao;

    @JsonProperty("dataViagem")
    private LocalDate dataViagem;

    @JsonProperty("publico")
    private Boolean publico = false;

    // Lista de pontos substituída por completo no update
    @Valid
    private List<RoteiroNoPontoRequestDto> pontos;
}