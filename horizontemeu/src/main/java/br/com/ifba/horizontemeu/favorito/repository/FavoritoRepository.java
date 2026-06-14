package br.com.ifba.horizontemeu.favorito.repository;

import br.com.ifba.horizontemeu.favorito.entity.Favorito;
import br.com.ifba.horizontemeu.pontoTuristico.entity.PontoTuristico;
import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {

    //Listar favoritos de um usuário
    List<Favorito> findByUsuario(Usuario usuario);

    // Verifica se o usuário já favoritou esse ponto turístico antes — usado para impedir duplicatas (RN03)
    Optional<Favorito> findByUsuarioAndPontoTuristico(Usuario usuario, PontoTuristico ponto);
}
