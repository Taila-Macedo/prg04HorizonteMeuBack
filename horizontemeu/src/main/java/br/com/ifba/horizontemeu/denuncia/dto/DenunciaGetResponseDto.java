package br.com.ifba.horizontemeu.denuncia.dto;

import br.com.ifba.horizontemeu.denuncia.enums.StatusDenuncia;
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
public class DenunciaGetResponseDto {

    @JsonProperty(value = "id")
    private Long id;

    @JsonProperty(value = "motivo")
    private String motivo;

    @JsonProperty(value = "status")
    private StatusDenuncia status;

    @JsonProperty(value = "data")
    private LocalDateTime data;

    @JsonProperty(value = "idUsuario")
    private Long idUsuario;

    @JsonProperty(value = "idFoto")
    private Long idFoto;

    @JsonProperty(value = "idComentario")
    private Long idComentario;

    @JsonProperty(value = "idUsuarioDenunciado")
    private Long idUsuarioDenunciado;
}