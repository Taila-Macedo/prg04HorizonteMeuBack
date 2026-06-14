package br.com.ifba.horizontemeu.favorito.controller;

import br.com.ifba.horizontemeu.favorito.dto.FavoritoPostRequestDto;
import org.springframework.http.ResponseEntity;

public interface FavoritoIController {
    ResponseEntity<?> findByUsuario(Long idUsuario);
    ResponseEntity<?> save(FavoritoPostRequestDto dto);
    ResponseEntity<?> delete(Long id);
}
