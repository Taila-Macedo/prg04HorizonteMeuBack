package br.com.ifba.horizontemeu.comentario.repository;

import br.com.ifba.horizontemeu.comentario.entity.Comentario;
import br.com.ifba.horizontemeu.pontoTuristico.entity.PontoTuristico;
import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    //buscar ponto Turístico
    List<Comentario> findByPontoTuristico(PontoTuristico pontoTuristico);

    //buscar por usuario
    List<Comentario> findByUsuario(Usuario usuario);
}
