package com.ifba.horizontemeu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;


/**
 * Entidade Usuario — representa qualquer pessoa cadastrada no Horizonte.
 * É a classe central do sistema.
 */

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long  id;

    //nome completo do usuário
    private String nome;

    //e-mail único no sistema
    @Column(unique = true, nullable = false)
    private String email;

    //senha
    private String senha;

    //url da foto do perfil
    private String fotoPerfil;

    //tipo de acesso ("Usuário" ou "ADM")
    private String perfil;

    // Data de cadastro
    private LocalDate dataCadastro;
}
