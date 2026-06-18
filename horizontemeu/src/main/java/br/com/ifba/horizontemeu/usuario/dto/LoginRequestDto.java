package br.com.ifba.horizontemeu.usuario.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO de ENTRADA para o endpoint de login (POST /auth/login).
 * Recebe apenas email e senha — o token é gerado e retornado no response.
 */
public class LoginRequestDto {

    @JsonProperty("email")
    @NotNull(message = "O e-mail é obrigatório!")
    @NotBlank(message = "O e-mail não pode ser vazio!")
    @Email(message = "E-mail inválido!")
    private String email;

    @JsonProperty("senha")
    @NotNull(message = "A senha é obrigatória!")
    @NotBlank(message = "A senha não pode ser vazia!")
    private String senha;
}
