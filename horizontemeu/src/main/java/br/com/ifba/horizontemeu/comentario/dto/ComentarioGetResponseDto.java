package br.com.ifba.horizontemeu.comentario.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComentarioGetResponseDto {

    @JsonProperty(value = "id")
    private Long id;

    @JsonProperty(value = "texto")
    private String texto;

    @JsonProperty(value = "fotoUrl")
    private String fotoUrl;

    @JsonProperty(value = "nota")
    private Integer nota;

    @JsonProperty(value = "curtidas")
    private Integer curtidas;

    @JsonProperty(value = "data")
    private LocalDateTime data;

    @JsonProperty(value = "editado")
    private Boolean editado;

    @JsonProperty(value = "idUsuario")
    private Long idUsuario;

    @JsonProperty(value = "idPontoTuristico")
    private Long idPontoTuristico;

    // NOVO (RN21) — indica se o usuário informado no PATCH /curtir já
    // curtiu este comentário. Fica null nas demais rotas (que não recebem idUsuario).
    @JsonProperty(value = "curtido")
    private Boolean curtido;
}