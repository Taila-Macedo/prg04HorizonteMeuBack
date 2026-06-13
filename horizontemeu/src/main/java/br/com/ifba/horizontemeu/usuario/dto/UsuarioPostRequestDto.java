package br.com.ifba.horizontemeu.usuario.dto;

import br.com.ifba.horizontemeu.usuario.enums.Perfil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
// DTO de ENTRADA — define quais campos o cliente pode enviar
// id e dataCadastro são omitidos pois são gerados automaticamente pelo sistema
public class UsuarioPostRequestDto {

    //nome do usuario
    @JsonProperty(value = "nome")
    @NotNull(message = "O nome é obrigatório!")
    @NotBlank(message = "O nome não pode ser vazio!")
    private String nome;


    @JsonProperty(value = "email")
    @NotNull(message = "O e-mail é obrigatório!")
    @NotBlank(message = "O e-mail não pode ser vazio!")
    @Email(message = "E-mail inválido!")
    private String email;

    @JsonProperty(value = "perfil")
    @NotNull(message = "O perfil é obrigatório!")
    private Perfil perfil;

    @JsonProperty(value = "fotoPerfil")
    private String fotoPerfil; // campo opcional, sem validação

    @JsonProperty(value = "senha")
    @NotNull(message = "A senha é obrigatória!")
    @NotBlank(message = "A senha não pode ser vazia!")
    @Size(min = 6, message = "A senha precisa ter pelo menos 6 caracteres!")
    private String senha;
}
