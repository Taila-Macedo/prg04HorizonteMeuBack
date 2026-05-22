package br.com.ifba.horizontemeu.usuario.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// DTO de ENTRADA — define quais campos o cliente pode enviar
// id e dataCadastro são omitidos pois são gerados automaticamente pelo sistema
public class UsuarioPostRequestDto {

    //nome do usuario
    @JsonProperty(value = "nome")
    private String nome;

    @JsonProperty(value = "email")
    private String email;

    @JsonProperty(value = "perfil")
    private String perfil;

    @JsonProperty(value = "fotoPerfil")
    private String fotoPerfil;

    @JsonProperty(value = "senha")
    private String senha;
}
