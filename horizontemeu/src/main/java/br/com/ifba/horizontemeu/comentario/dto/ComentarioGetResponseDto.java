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
}
