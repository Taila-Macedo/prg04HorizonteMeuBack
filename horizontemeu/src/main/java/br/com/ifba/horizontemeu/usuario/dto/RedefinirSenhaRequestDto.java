package br.com.ifba.horizontemeu.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Etapa 3 — redefinir a senha.
 * POST /auth/recuperar-senha/redefinir
 *
 */
public record RedefinirSenhaRequestDto(

        @NotBlank(message = "E-mail é obrigatório.")
        String email,

        @NotBlank(message = "Código é obrigatório.")
        @Size(min = 6, max = 6, message = "O código deve ter 6 dígitos.")
        String codigo,

        @NotBlank(message = "Nova senha é obrigatória.")
        @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres.")
        String novaSenha
) {}