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
/**
 * DTO de SAÍDA para respostas com dados do ponto turístico.
 * O "id" é incluído pois o front precisa dele para compor
 * outras requisições (fotos, comentários, favoritos, roteiros).
 * "notaMedia" não é enviada no cadastro — começa em 0.0 e é
 * recalculada automaticamente a cada comentário (RN04).
 */
public class PontoTuristicoGetResponseDto {

    @JsonProperty("id")
    private Long id;

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

    // Recalculada automaticamente — só leitura, nunca enviada pelo cliente
    @JsonProperty(value = "notaMedia")
    private Float notaMedia;

    @JsonProperty(value = "categoria")
    private Categoria categoria;

    // NOVO: o front usa isso para decidir quais pontos mostrar no mapa
    @JsonProperty(value = "noMapa3D")
    private Boolean noMapa3D;
}
