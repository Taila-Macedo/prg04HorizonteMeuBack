package br.com.ifba.horizontemeu.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlterarSenhaRequestDto(
        @NotBlank String senhaAtual,
        @NotBlank @Size(min = 6) String novaSenha
) {}