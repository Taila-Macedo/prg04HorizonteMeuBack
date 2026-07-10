package br.com.ifba.horizontemeu.denuncia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de entrada para registrar uma denúncia (POST /denuncias).
 * Apenas UM dos três campos idFoto / idComentario / idUsuarioDenunciado
 * deve vir preenchido — o service valida isso.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DenunciaPostRequestDto {

    @JsonProperty(value = "motivo")
    @NotNull(message = "O motivo da denúncia é obrigatório!")
    @NotBlank(message = "O motivo da denúncia não pode ser vazio!")
    private String motivo;

    @JsonProperty(value = "idUsuario")
    @NotNull(message = "O id do usuáario que denuncia é obrigatório!")
    private Long idUsuario;

    @JsonProperty(value = "idFoto")
    private Long idFoto;

    @JsonProperty(value = "idComentario")
    private Long idComentario;

    @JsonProperty(value = "idUsuarioDenunciado")
    private Long idUsuarioDenunciado;
}
