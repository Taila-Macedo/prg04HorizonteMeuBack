package br.com.ifba.horizontemeu.usuario.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de ENTRADA para cadastro de novo usuário (POST /usuarios/save).
 *
 * Não expõe o campo "perfil" — todo usuário cadastrado via API
 * recebe perfil USUARIO por padrão. Promover para ADMINISTRADOR
 * é uma operação separada, restrita ao admin.
 *
 * Também não expõe "id" nem "dataCadastro" — gerados automaticamente.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioPostRequestDto {

    @JsonProperty("nome")
    @NotNull(message = "O nome é obrigatório!")
    @NotBlank(message = "O nome não pode ser vazio!")
    private String nome;

    @JsonProperty("email")
    @NotNull(message = "O e-mail é obrigatório!")
    @NotBlank(message = "O e-mail não pode ser vazio!")
    @Email(message = "E-mail inválido!")
    private String email;

    @JsonProperty("senha")
    @NotNull(message = "A senha é obrigatória!")
    @NotBlank(message = "A senha não pode ser vazia!")
    @Size(min = 6, message = "A senha precisa ter pelo menos 6 caracteres!")
    private String senha;

    // Campo opcional — usuário pode enviar a URL da foto de perfil no cadastro
    @JsonProperty("fotoPerfil")
    private String fotoPerfil;
}