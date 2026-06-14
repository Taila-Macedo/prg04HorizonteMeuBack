package br.com.ifba.horizontemeu.favorito.service;

import br.com.ifba.horizontemeu.favorito.entity.Favorito;
import java.util.List;

public interface FavoritoIService {
    List<Favorito> findByUsuario(Long idUsuario);
    Favorito save(Favorito favorito);
    void delete(Long id);
}
