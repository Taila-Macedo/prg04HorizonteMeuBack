package br.com.ifba.horizontemeu.roteiroponto.entity;

import br.com.ifba.horizontemeu.infrastructure.entity.PersistenceEntity;
import br.com.ifba.horizontemeu.pontoTuristico.entity.PontoTuristico;
import br.com.ifba.horizontemeu.roteiro.entity.Roteiro;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roteiro_pontos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
/**
 * Entidade intermediária que vincula um Ponto Turístico a um Roteiro específico.
 * Serve para organizar a estrutura do itinerário, armazenando a ordem de visita
 * de cada parada e controlando o checklist de locais que o usuário já visitou.
 */
public class RoteiroNoPonto extends PersistenceEntity{

    @ManyToOne
    @JoinColumn(name = "id_roteiro", nullable = false)
    private Roteiro roteiro;

    @ManyToOne
    @JoinColumn(name = "id_ponto_turistico", nullable = false)
    private PontoTuristico pontoTuristico;

    @Column(nullable = false)
    private Integer ordem;

    @Column(nullable = false)
    private Boolean visitado = false;
}
