package br.com.ifba.horizontemeu.roteiro.entity;

import br.com.ifba.horizontemeu.infrastructure.entity.PersistenceEntity;
import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "roteiros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Roteiro extends PersistenceEntity {

    @Column(nullable = false)
    private String titulo;

    private String descricao;
    private LocalDate dataViagem;
    private LocalDate dataCriacao;

    @Column(nullable = false)
    private Boolean publico = false;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
}
