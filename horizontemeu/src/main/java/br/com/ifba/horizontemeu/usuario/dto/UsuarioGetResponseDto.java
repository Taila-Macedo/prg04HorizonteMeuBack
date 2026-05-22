package br.com.ifba.horizontemeu.usuario.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// DTO de SAÍDA — define quais campos são retornados ao cliente
// senha e id são omitidos propositalmente por segurança
public class UsuarioGetResponseDto {

    //nome do usuario
    @JsonProperty(value = "nome")
    private String nome;

    @JsonProperty(value = "email")
    private String email;

    @JsonProperty(value = "perfil")
    private String perfil;

    @JsonProperty(value = "fotoPerfil")
    private String fotoPerfil;
}
