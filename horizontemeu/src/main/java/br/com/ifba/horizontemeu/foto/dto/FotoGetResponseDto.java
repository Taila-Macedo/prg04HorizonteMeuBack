package br.com.ifba.horizontemeu.foto.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO de SAÍDA para respostas com dados da foto.
 *
 * Campos incluídos:
 *   - id          — front precisa para montar URLs de denúncia e exclusão
 *   - idUsuario   — front precisa para saber quem enviou
 *   - idPonto     — front precisa para montar a galeria do ponto
 *   - aprovado    — front usa para mostrar badge "pendente" ou "aprovada"
 *
 * Campos omitidos propositalmente:
 *   - objeto Usuario completo — evita expor dados sensíveis do usuário
 *   - objeto PontoTuristico completo — evita resposta muito grande
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FotoGetResponseDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("url")
    private String url;

    @JsonProperty("legenda")
    private String legenda;

    @JsonProperty("dataUpload")
    private LocalDate dataUpload;

    // Front usa para mostrar badge "pendente de aprovação" ou "aprovada"
    @JsonProperty("aprovado")
    private Boolean aprovado;

    @JsonProperty("idUsuario")
    private Long idUsuario;

    @JsonProperty("idPontoTuristico")
    private Long idPontoTuristico;
}