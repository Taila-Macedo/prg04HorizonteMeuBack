package br.com.ifba.horizontemeu.pontoTuristico.entity;

import br.com.ifba.horizontemeu.infrastructure.entity.PersistenceEntity;
import br.com.ifba.horizontemeu.pontoTuristico.enums.Categoria;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Entidade que representa um ponto turístico cadastrado na plataforma.
 * Contém informações sobre o local, como nome, descrição, localização geográfica, categoria e média de avaliação.
 * Pontos turísticos são cadastrados apenas pelo adm.
 */
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

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false)
    private String pais;

    //Média das avaliações (0.0 a 5.0) — recalculada automaticamente
    private Float notaMedia;

    @Column(nullable = false)
    private Float latitude;

    @Column(nullable = false)
    private Float longitude;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    // NOVO: indica se o ponto deve aparecer no mapa 3D da dashboard
    @Column(name = "no_mapa3d", nullable = false, columnDefinition = "boolean default false")
    private Boolean noMapa3D;

}