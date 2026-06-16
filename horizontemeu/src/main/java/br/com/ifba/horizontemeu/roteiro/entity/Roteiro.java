package br.com.ifba.horizontemeu.roteiro.entity;

import br.com.ifba.horizontemeu.infrastructure.entity.PersistenceEntity;
import br.com.ifba.horizontemeu.roteiroponto.entity.RoteiroNoPonto;
import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roteiros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
/**
 * Entidade que representa o cabeçalho de um roteiro de viagem criado pelo usuário.
 * Guarda as informações principais do planejamento, como o título, a descrição,
 * a data da viagem e se o roteiro é público para outros usuários visualizarem.
 */
public class Roteiro extends PersistenceEntity {

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    private LocalDate dataViagem;
    private LocalDate dataCriacao;

    @Column(nullable = false)
    private Boolean publico = false;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    // Cascade: ao salvar/deletar um Roteiro, os RoteiroNoPonto são salvos/deletados juntos
    @OneToMany(mappedBy = "roteiro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoteiroNoPonto> pontos = new ArrayList<>();
}
