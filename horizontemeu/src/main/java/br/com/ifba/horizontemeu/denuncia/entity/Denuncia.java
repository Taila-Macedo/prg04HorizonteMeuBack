package br.com.ifba.horizontemeu.denuncia.entity;

import br.com.ifba.horizontemeu.comentario.entity.Comentario;
import br.com.ifba.horizontemeu.denuncia.enums.StatusDenuncia;
import br.com.ifba.horizontemeu.foto.entity.Foto;
import br.com.ifba.horizontemeu.infrastructure.entity.PersistenceEntity;
import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidade que representa uma denúncia feita por um usuário.
 * Apenas uma das três FKs (foto, comentario,
 * usuarioDenunciado) fica preenchida por vez; as outras ficam null.
 */
@Entity
@Table(name = "denuncias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Denuncia extends PersistenceEntity {

    @Column(nullable = false)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusDenuncia status;

    private LocalDateTime data;

    //Quem faz a denúncia
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    // Preenchido quando o conteúdo denunciado é uma foto
    @ManyToOne
    @JoinColumn(name = "id_foto")
    private Foto foto;

    // Preenchido quando o conteúdo denunciado é um comentário
    @ManyToOne
    @JoinColumn(name = "id_comentario")
    private Comentario comentario;

    // Preenchido quando o que é denunciado é um perfil de usuário
    @ManyToOne
    @JoinColumn(name = "id_usuario_denunciado")
    private Usuario usuarioDenunciado;
}
