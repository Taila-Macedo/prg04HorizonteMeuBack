package br.com.ifba.horizontemeu.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Etapa 2 — validar código de 6 dígitos.
 * POST /auth/recuperar-senha/validar
 *
 */
public record ValidarCodigoRequestDto(

        @NotBlank(message = "E-mail é obrigatório.")
        String email,

        @NotBlank(message = "Código é obrigatório.")
        @Size(min = 6, max = 6, message = "O código deve ter 6 dígitos.")
        String codigo
) {}