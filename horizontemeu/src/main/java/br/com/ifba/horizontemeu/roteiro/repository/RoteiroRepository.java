package br.com.ifba.horizontemeu.roteiro.repository;

import br.com.ifba.horizontemeu.roteiro.entity.Roteiro;
import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoteiroRepository extends JpaRepository<Roteiro, Long> {
    // Busca exclusivamente os roteiros pertencentes a um usuário específico.
    List<Roteiro> findByUsuario(Usuario usuario);
}
