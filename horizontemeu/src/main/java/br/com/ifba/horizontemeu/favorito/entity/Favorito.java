package br.com.ifba.horizontemeu.favorito.entity;

import br.com.ifba.horizontemeu.infrastructure.entity.PersistenceEntity;
import br.com.ifba.horizontemeu.pontoTuristico.entity.PontoTuristico;
import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Entidade que representa um ponto turístico salvo
 * por um usuário na sua lista de favoritos.
 * O par (usuario, pontoTuristico) deve ser único —
 * um usuário não pode favoritar o mesmo ponto duas vezes.
 */
@Entity
@Table(name = "favoritos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Favorito extends PersistenceEntity {

    private LocalDate dataSalvo;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_ponto_turistico", nullable = false)
    private PontoTuristico pontoTuristico;
}
