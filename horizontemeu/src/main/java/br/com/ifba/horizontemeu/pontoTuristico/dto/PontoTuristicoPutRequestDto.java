package br.com.ifba.horizontemeu.pontoTuristico.dto;

import br.com.ifba.horizontemeu.pontoTuristico.enums.Categoria;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de ENTRADA para atualização de ponto turístico (PUT /pontos/{id}).
 *
 * Separado do PontoTuristicoPostRequestDto intencionalmente:
 *   - "notaMedia" nunca é atualizada diretamente pelo admin — é recalculada
 *     automaticamente pelo sistema via calcularNota() (RN04)
 *   - Deixa claro quais campos podem ser editados após o cadastro
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PontoTuristicoPutRequestDto {

    @JsonProperty("nome")
    @NotNull(message = "O nome é obrigatório!")
    @NotBlank(message = "O nome não pode ser vazio!")
    private String nome;

    // Descrição é opcional — ponto pode não ter descrição detalhada ainda
    @JsonProperty("descricao")
    private String descricao;

    @JsonProperty("cidade")
    @NotNull(message = "A cidade é obrigatória!")
    @NotBlank(message = "A cidade não pode ser vazia!")
    private String cidade;

    @JsonProperty("pais")
    @NotNull(message = "O país é obrigatório!")
    @NotBlank(message = "O país não pode ser vazio!")
    private String pais;

    @JsonProperty("latitude")
    @NotNull(message = "A latitude é obrigatória!")
    private Float latitude;

    @JsonProperty("longitude")
    @NotNull(message = "A longitude é obrigatória!")
    private Float longitude;

    @JsonProperty("categoria")
    @NotNull(message = "A categoria é obrigatória!")
    private Categoria categoria;

    // NOVO: permite ligar/desligar o ponto do mapa 3D na edição
    @JsonProperty("noMapa3D")
    private Boolean noMapa3D;
}