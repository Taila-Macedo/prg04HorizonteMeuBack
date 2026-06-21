package br.com.ifba.horizontemeu.comentario.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de ENTRADA para atualização de comentário (PUT /comentarios/{id}).
 *
 * Separado do ComentarioPostRequestDto intencionalmente:
 *   - nota não pode ser alterada após publicação — avaliação é imutável
 *   - idUsuario e idPontoTuristico não mudam — o comentário pertence ao mesmo
 *     usuário e ponto para sempre
 *
 * Só texto e fotoUrl podem ser editados (RN — editado = true após update).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComentarioPutRequestDto {

    @JsonProperty(value = "texto")
    @NotNull(message = "O texto do comentário é obrigatório!")
    @NotBlank(message = "O texto do comentário não pode ser vazio!")
    private String texto;

    // Campo opcional — usuário pode remover ou trocar a foto do comentário
    @JsonProperty(value = "fotoUrl")
    private String fotoUrl;
}
