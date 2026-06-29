package br.com.ifba.horizontemeu.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Etapa 1 — solicitar código de recuperação.
 * POST /auth/recuperar-senha/solicitar
 *
 */
public record SolicitarCodigoRequestDto(

        @NotBlank(message = "E-mail é obrigatório.")
        @Email(message = "E-mail inválido.")
        String email
) {}