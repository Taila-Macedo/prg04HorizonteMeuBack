package br.com.ifba.horizontemeu.usuario.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO de SAÍDA para o endpoint de login (POST /auth/login).
 *
 * Retorna o token JWT e as informações básicas do usuário autenticado
 * para que o front-end possa exibir o nome/perfil sem precisar
 * fazer uma segunda requisição.
 */
public class LoginResponseDto {

    // Token JWT que o front deve guardar e enviar em cada requisição
    // no cabeçalho: Authorization: Bearer <token>
    @JsonProperty("token")
    private String token;

    // Dados do usuário autenticado — evita uma segunda chamada ao /usuarios/{id}
    @JsonProperty("id")
    private Long id;

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("email")
    private String email;

    @JsonProperty("perfil")
    private String perfil;
}
