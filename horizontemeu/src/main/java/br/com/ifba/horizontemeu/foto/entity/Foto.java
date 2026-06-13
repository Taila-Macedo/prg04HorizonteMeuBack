package br.com.ifba.horizontemeu.foto.entity;

import br.com.ifba.horizontemeu.infrastructure.entity.PersistenceEntity;
import br.com.ifba.horizontemeu.pontoTuristico.entity.PontoTuristico;
import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table (name = "fotos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Foto extends PersistenceEntity {

    @Column(nullable = false)
    private String url;

    private String legenda;
    private LocalDate dataUpload;

    @Column(nullable = false)
    private Boolean aprovado;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_ponto_turistico", nullable = false)
    private PontoTuristico pontoTuristico;
}
