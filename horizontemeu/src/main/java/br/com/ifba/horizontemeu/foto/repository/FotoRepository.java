package br.com.ifba.horizontemeu.foto.repository;

import br.com.ifba.horizontemeu.foto.entity.Foto;
import br.com.ifba.horizontemeu.pontoTuristico.entity.PontoTuristico;
import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FotoRepository extends JpaRepository<Foto, Long> {

    //lista fotos de um ponto
    List<Foto> findByPontoTuristico(PontoTuristico pontoTuristico);

    //Busca todas as fotos de um usuario
    List<Foto> findByUsuario(Usuario usuario);

    //busca fotos aprovadas ou pendentes
    List<Foto> findByAprovado(Boolean aprovado);

    List<Foto> findByPontoTuristicoAndAprovado(PontoTuristico pontoTuristico, Boolean aprovado);
}