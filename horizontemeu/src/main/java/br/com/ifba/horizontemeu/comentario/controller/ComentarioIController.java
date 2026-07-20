package br.com.ifba.horizontemeu.comentario.controller;

import br.com.ifba.horizontemeu.comentario.dto.ComentarioPostRequestDto;
import br.com.ifba.horizontemeu.comentario.dto.ComentarioPutRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface ComentarioIController {
    ResponseEntity<?> findAll(Pageable pageable);
    ResponseEntity<?> findById(Long id);
    ResponseEntity<?> findByPontoTuristico(Long idPonto);
    ResponseEntity<?> findByUsuario(Long idUsuario);
    ResponseEntity<?> save(ComentarioPostRequestDto dto);
    ResponseEntity<?> update(Long id, ComentarioPutRequestDto dto);
    ResponseEntity<?> curtir(Long id, Long idUsuario);
    ResponseEntity<?> delete(Long id);
}