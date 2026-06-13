package br.com.ifba.horizontemeu.usuario.entity;

import br.com.ifba.horizontemeu.infrastructure.entity.PersistenceEntity;
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
    private String perfil;

    // Data de cadastro
    private LocalDate dataCadastro;
}