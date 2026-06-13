package br.com.ifba.horizontemeu.pontoTuristico.entity;

import br.com.ifba.horizontemeu.infrastructure.entity.PersistenceEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pontos_turisticos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PontoTuristico extends PersistenceEntity {

    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    private String cidade;
    private String pais;
    private Float latitude;
    private Float longitude;
    private String categoria;
    private Float notaMedia;
}
