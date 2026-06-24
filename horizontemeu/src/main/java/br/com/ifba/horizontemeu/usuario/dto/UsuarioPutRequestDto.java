package br.com.ifba.horizontemeu.usuario.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de ENTRADA para atualização de perfil (PUT /usuarios/update/{id}).
 *
 * Separado do UsuarioPostRequestDto intencionalmente:
 *   - Email não pode ser alterado por aqui (operação sensível, precisaria de fluxo próprio)
 *   - Senha não pode ser alterada por aqui (usa o fluxo de reset de senha)
 *   - Perfil nunca é alterado diretamente pelo usuário
 *
 * Só expõe o que o usuário pode editar no próprio perfil.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioPutRequestDto {

    @JsonProperty("nome")
    @NotNull(message = "O nome é obrigatório!")
    @NotBlank(message = "O nome não pode ser vazio!")
    private String nome;

    // URL da nova foto de perfil — opcional
    @JsonProperty("fotoPerfil")
    private String fotoPerfil;

    @JsonProperty("bio")
    private String bio;

}
