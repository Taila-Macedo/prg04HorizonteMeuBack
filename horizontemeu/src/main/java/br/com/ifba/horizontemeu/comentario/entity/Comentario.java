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
