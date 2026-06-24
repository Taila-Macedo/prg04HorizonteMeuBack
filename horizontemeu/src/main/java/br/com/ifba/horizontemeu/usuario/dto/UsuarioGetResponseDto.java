package br.com.ifba.horizontemeu.usuario.dto;

import br.com.ifba.horizontemeu.usuario.enums.Perfil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * DTO de SAÍDA para respostas com dados do usuário.
 *
 * Campos omitidos propositalmente por segurança:
 *   - senha       — nunca deve trafegar, mesmo em hash
 *   - tokenResetSenha / tokenExpiracao — dados internos de segurança
 *
 * O "id" agora é incluído pois o front-end precisa dele para compor
 * outras requisições (ex: buscar favoritos, comentários, roteiros do usuário).
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioGetResponseDto {

    // Incluído — o front precisa do ID para montar URLs de outras entidades
    @JsonProperty("id")
    private Long id;

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("email")
    private String email;

    @JsonProperty("perfil")
    private Perfil perfil;

    @JsonProperty("fotoPerfil")
    private String fotoPerfil;

    @JsonProperty("bio")
    private String bio;
}