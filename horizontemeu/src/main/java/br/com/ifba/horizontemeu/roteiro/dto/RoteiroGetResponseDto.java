package br.com.ifba.horizontemeu.roteiro.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO de SAÍDA para respostas com dados do roteiro.
 * Inclui publico para o front saber se pode exibir link de compartilhamento.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoteiroGetResponseDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("titulo")
    private String titulo;

    @JsonProperty("descricao")
    private String descricao;

    @JsonProperty("dataViagem")
    private LocalDate dataViagem;

    @JsonProperty("dataCriacao")
    private LocalDate dataCriacao;

    // Se true o front exibe o link de compartilhamento (RN16)
    @JsonProperty("publico")
    private Boolean publico;

    // ID do dono do roteiro — front precisa para saber se pode editar
    @JsonProperty("idUsuario")
    private Long idUsuario;

    // Lista de pontos do roteiro com ordem, visitado e nome do ponto
    @JsonProperty("pontos")
    private List<RoteiroNoPontoResponseDto> pontos;
}