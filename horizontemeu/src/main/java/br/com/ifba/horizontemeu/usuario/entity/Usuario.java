package br.com.ifba.horizontemeu.usuario.entity;

import br.com.ifba.horizontemeu.infrastructure.entity.PersistenceEntity;
import br.com.ifba.horizontemeu.usuario.enums.Perfil;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade Usuario — representa qualquer pessoa cadastrada no Horizonte.
 * É a classe central do sistema.
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario extends PersistenceEntity {

    // Nome completo do usuário
    private String nome;

    // E-mail único no sistema
    @Column(unique = true, nullable = false)
    private String email;

    // Senha
    private String senha;

    // URL da foto do perfil
    private String fotoPerfil;

    // Tipo de acesso ("usuario" ou "adm")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Perfil perfil;

    // Data de cadastro
    private LocalDate dataCadastro;

    //Token temporário para recuperação de senha (null quando inativo)
    private String tokenResetSenha;

    //Data/hora de expiração do token de reset (expira em 1 hora)
    private LocalDateTime tokenExpiracao;
}