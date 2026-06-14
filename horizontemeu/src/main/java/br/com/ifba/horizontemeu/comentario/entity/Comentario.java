package br.com.ifba.horizontemeu.comentario.entity;

import br.com.ifba.horizontemeu.infrastructure.entity.PersistenceEntity;
import br.com.ifba.horizontemeu.pontoTuristico.entity.PontoTuristico;
import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidade que representa um comentário publicado por um usuário
 * em um ponto turístico. Inclui nota de 1 a 5 estrelas, contador
 * de curtidas e um campo opcional foto_url para anexar uma imagem
 * diretamente no comentário (independente da classe Foto).
 */
@Entity
@Table (name = "comentarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comentario extends PersistenceEntity {

    @Column(nullable = false)
    private String texto;

    private String fotoUrl;

    @Column(nullable = false)
    private Integer nota;

    @Column(nullable = false)
    private Integer  curtidas = 0;

    private LocalDateTime data;

    @Column(nullable = false)
    private Boolean editado = false;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_ponto_turistico", nullable = false)
    private PontoTuristico pontoTuristico;
}
